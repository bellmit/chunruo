<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('Image', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
    fields: [
    	{name: 'id',	 		mapping: 'id',			type: 'int'},
		{name: 'type',	 		mapping: 'type',		type: 'int'},
		{name: 'url',	 		mapping: 'url',			type: 'string'},
		{name: 'imageName',	 	mapping: 'imageName',			type: 'string'},
		{name: 'uploadTime',	mapping: 'uploadTime',	type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}}
	]
});

Ext.define('MyExt.storeManager.ChannelImagesPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
    id:'imageListPanel',
   	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
        
        this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Image',
			id:'channelImagesPanel',
			proxy: {
				type: 'ajax',
				url: '/channel/imageList.json?typeId=' + this.typeId,
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'uploadTime',
	            direction: 'desc'
	        }]
		});
		
	
        
   
		this.columns = [
			{text: '<fmt:message key="jkd.image.id"/>', dataIndex: 'id', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="jkd.image.url"/>', dataIndex: 'url', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.rendererImageUrl
        	},
        	{text: '<fmt:message key="jkd.image.type"/>', dataIndex: 'type', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.rendererImageType
        	},
        	{text: '<fmt:message key="jkd.image.name"/>', dataIndex: 'imageName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="jkd.image.uploadTime"/>', dataIndex: 'uploadTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
        ];
        
        this.pagingToolbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoheigth: true,
			displayInfo: true,
			displayMsg: '<fmt:message key="ajax.record"/>',
			emptyMsg: '<fmt:message key="ajax.no.record"/>',
			scope: this,
			items: ['-',{ 
				xtype: 'numberfield', 
				width: 120, 
				labelWidth: 65,
				value: 50, 
				minValue: 1, 
				fieldLabel: '<fmt:message key="ajax.record.size"/>',
                allowBlank: false,
               	scope: this,
                listeners:{
                	scope: this,
               		change: function (field, newValue, oldValue) {
                    	var number = parseInt(newValue);
                        if (isNaN(number) || !number || number < 1) {
                        	number = 50;
                           	Field.setValue(number);
                        }
                       	this.store.pageSize = number;
                       	this.store.load();
                   	}
               	}
        	}]	
		});
		this.imageListBbar = this.pagingToolbar; 
		
    	this.imageList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'imageList@ImagePanel' + this.id,
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
		    enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    bbar: this.imageListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	 
	    this.tbar = [{
        	text: '<fmt:message key="image.cancel"/>', 
        	iconCls: 'delete', 	
        	handler: this.cancelImage, 
        	scope: this,
        	hidden: this.selectImage
        },'->',{
        	text: '<fmt:message key="fx.page.upload_image"/>', 
        	iconCls: 'upload', 	
        	handler: this.addImage, 
        	scope: this,
        	hidden: this.selectImage
        }];
        
     
	    
    	this.items = [this.imageList];	

    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.imageList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.imageList.getSelectionModel();
	    
    },
    
    cancelImage : function(){
    
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.id);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="image.cancel"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/cancelImage.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    },
    
    rendererImageType : function(val){
		var str =  "";
		if(val == 1){
			str = '<fmt:message key="fx.page.add.modular.2"/>';
		}else if(val==2){
			str = '<fmt:message key="fx.page.add.modular.3"/>';
		}else if(val==3){
			str = '<fmt:message key="fx.page.add.modular.4"/>';
		}
		return str;
	},
	
   rendererImageUrl : function(val, metadata, record, rowIndex, columnIndex, store){ 
					var str =  "";
					val = 'http://www.jikeduo.com.cn/depository/' + val;
					//val = 'http://127.0.0.1:8080/depository/' + val;
					if(record.data.type == 1){
						str = '<img src="'+ val +'" width="100" height="50"/>';
					}else if(record.data.type == 2){
						str = '<img src="'+ val +'" width="100" height="30"/>';
					}else if(record.data.type == 3){
						str = '<img src="'+ val +'" width="50" height="50"/>';
					}
					return str;
	},

  
	

 addImage : function() {
		
    var form = new Ext.form.Panel({
        id:"addOImage",
        border: false,
        fileUpload: true,
        fieldDefaults: {
            labelWidth: 70,
            labelAlign:'right',
            labelStyle:'padding-right:10px'
        },
        layout:'column',
        bodyPadding: 10,
            items: [{
				  	labelWidth: 85,
				  	id : 'typeId',
		       		width: 300,
		            name: 'typeId',
		            fieldLabel:'<fmt:message key="image.fileType"/>',
					xtype:'combo',
					valueField:'key',
					style:"text-align:center",
					displayField:'value',
					emptyText : '<fmt:message key="button.select"/>', 
					hiddenName:'categoryType',
					store: new Ext.data.ArrayStore({  
								fields : ['key', 'value'],  
		                        data : [["1", '<fmt:message key="fx.page.add.modular.2" />'],
		                        		["2", '<fmt:message key="fx.page.add.modular.3" />'],
		                               	["3", '<fmt:message key="fx.page.add.modular.4" />']]  
		                    }), 
					mode : 'local',
					triggerAction : 'all'
				},{
					 xtype: 'textfield',
					 id:'imageName',
					 style:"margin-top:10px;text-align:center",
           			fieldLabel: '<fmt:message key="jkd.image.name" />',
           			labelWidth: 85,
           			name: 'imageName',
           			readOnly: false,
           			 width:300
                
				},{  
		           xtype: 'filefield',  
		           name: 'photo',  
		           style:"margin-top:10px;text-align:center",
		           fieldLabel: '<fmt:message key="image.select"/>',  
		           msgTarget: 'side',  
		           allowBlank: false,  
		           width:300,  
		           emptyText: '<fmt:message key="image.addFile"/>',
		           buttonText: '<fmt:message key="image.addFile"/>',
		        }
        ],  
        
    });
    var window = new Ext.window.Window({
        modal: true,
        autoShow: true,
        title:  '<fmt:message key="image.addFile"/>',
        width: 490,
        height:420,
        minWidth: 300,
        minHeight: 200,
        layout: 'fit',
        items: form,
        buttons: [{
            text: '<fmt:message key="button.save"/>',
            handler: function () {
                if (!Ext.getCmp("addOImage").getForm().isValid()) {
                  
                    return false;
                }
                if(!Ext.getCmp("typeId").value){
                	showFailMsg('<fmt:message key="image.type.null"/>', 4);
                    return false;
                }
                 if(!Ext.getCmp("imageName").value){
	                 if(Ext.getCmp("typeId").value == '3'){
		                 showFailMsg('<fmt:message key="image.name.null"/>', 4);
	                    return false;
	                    }
                }
                form.getForm().submit({
                    url:'/channel/uploadImage.json?typeId=' + Ext.getCmp("typeId").value + '&imageName=' + Ext.getCmp("imageName").value,
                    waitMsg:"loading....",
                  	success:function(form,action){
                        var res = action.result;
                        if(res.success == true){
                           	showSuccMsg(res.message);
                        //   console.log(Ext.getCmp('imageListPanel'));
                        // 	Ext.getCmp('imageListPanel').store.load();
                          	window.close();
                        }
                    },
                    failure:function(form,action){
                       showFailMsg('<fmt:message key="image.noupload.error"/>', 4);
                    }
                }); 
            }
        },{
            text: '<fmt:message key="button.cancel"/>',
            handler: function () {
                window.close();
            }
        }]
    });
            
},




 
});