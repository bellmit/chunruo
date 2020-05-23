<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('TrainTeacher', {
	extend: 'Ext.data.Model',
	idProperty: 'teacherId',
    fields: [
		{name: 'teacherId',		     mapping: 'teacherId',		              type: 'int'},
		{name: 'nickName',		     mapping: 'nickName',		              type: 'string'},
		{name: 'wechatNumber',		 mapping: 'wechatNumber',		          type: 'string'},
		{name: 'headerImage',		 mapping: 'headerImage',		          type: 'string'},
		{name: 'type',		         mapping: 'type',		                  type: 'int'},
		{name: 'qrCode',	         mapping: 'qrCode',                       type: 'string'},
		{name: 'createTime',	 	 mapping: 'createTime',		              type: 'string'},
		{name: 'updateTime',	 	 mapping: 'updateTime',		              type: 'string'}
    ]
});

Ext.define('MyExt.couponManager.TrainTeacherListPanel', {
   	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
    },
    
	initComponent : function(config) {
	    Ext.apply(this, config);
		
		var now = new Date();
   		var expiry = new Date(now.getTime() + 10 * 60 * 1000);
   		Ext.util.Cookies.set('isCheck','0',expiry);
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'TrainTeacher',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/trainTeacher/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
		
		 this.levelStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
			    {id: '1', name: '<fmt:message key="invites.courtesy.level1"/>'},
				{id: '2', name: '<fmt:message key="invites.courtesy.level2"/>'}
			]
		});
		
		this.renderertypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="train.teacher.type0"/>'},
        		{id: 1, name: '<fmt:message key="train.teacher.type1"/>'},
        	]
       	});
		
		this.columns = [
			{text: '<fmt:message key="train.teacher.teacherId"/>',  dataIndex: 'teacherId', width: 100,  sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="train.teacher.nickName"/>',  dataIndex: 'nickName', width: 100,  sortable: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="train.teacher.wechatNumber"/>',  dataIndex: 'wechatNumber', width: 100,  sortable: true, filter: {xtype: 'textfield'}},
            {text: '<fmt:message key="train.teacher.type"/>', dataIndex: 'type', width: 160, sortable : true, 
        		renderer : this.renderertypeStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.renderertypeStore,
			        queryMode: 'local',
			        typeAhead: true
				},
        	}, 
            {text: '<fmt:message key="train.teacher.status"/>', dataIndex: 'status', width: 100, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == false) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	},
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			         store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="button.yes"/>'},
							{id: '0', name: '<fmt:message key="button.no"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
             {
            	text: '<fmt:message key="train.teacher.headerImage"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 260,
                higth: 160,
                dataIndex: 'headerImage',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            },
            {
            	text: '<fmt:message key="train.teacher.qrCode"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 260,
                higth: 160,
                dataIndex: 'qrCode',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            },
			{text: '<fmt:message key="train.teacher.createTime"/>',        dataIndex: 'createTime', width: 150, sortable: true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="train.teacher.updateTime"/>',        dataIndex: 'updateTime', width: 150, sortable: true, filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/trainTeacher/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/trainTeacher/saveTrainTeacher.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="discovery.save"/>', 
        	iconCls: 'enable', 	
        	handler: this.saveTrainTeacher, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/trainTeacher/deleteTrainTeacher.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="discovery.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteTrainTeacher, 
        	scope: this
        }
        </jkd:haveAuthorize>
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
		
		this.productList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.pagingToolbar,
		    plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.couponManager.TrainTeacherTabPanel', {
        	productList: this.productList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
    	
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/trainTeacher/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/trainTeacher/getTrainTeacherById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
    },
    
    saveTrainTeacher : function(){
    	var trainTeacherFormPanel = Ext.create('MyExt.couponManager.TrainTeacherFormPanel', {
			id: 'add@trainTeacherFormPanel' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	var rowsData = [];
		    	var rowsDatas = [];
		       	trainTeacherFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsData.push(record.data);    
		      	}, this);
		      	
		       	trainTeacherFormPanel.down('imagepanels').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
		      
		    	if(trainTeacherFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		trainTeacherFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/trainTeacher/saveTrainTeacher.json"/>',
		               			scope: this,
		               			params:{headerImageJson: Ext.JSON.encode(rowsData),qrCodeJson: Ext.JSON.encode(rowsDatas)},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.load();
		                  				popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
		               			}
		   					})
		   				}
		   			}, this)
		 		}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.add"/>', trainTeacherFormPanel, buttons,300, 560);
    },    
    
    deleteTrainTeacher : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.teacherId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm.agree"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/trainTeacher/deleteTrainTeacher.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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
 
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	couponTypeRenderer : function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="coupon.couponType1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="coupon.couponType2"/></b>';
         }
   	},
   	
   	levelRenderer: function(val){
   	    if(val == 1) {
            return '<b><fmt:message key="invites.courtesy.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="invites.courtesy.level2"/></b>';
         }
   	},
   	
   	attributeRenderer : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="order.evaluate.status0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="order.evaluate.status1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="order.evaluate.status2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="order.evaluate.status3"/></b>';
         }
   	},
	
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
	
		renderertypeStuts : function(val){
   	    if(val == 0) {
            return '<b><fmt:message key="train.teacher.type0"/></b>';
         }else if(val == 1){
            return '<b><fmt:message key="train.teacher.type1"/></b>';
         }
   	},
});