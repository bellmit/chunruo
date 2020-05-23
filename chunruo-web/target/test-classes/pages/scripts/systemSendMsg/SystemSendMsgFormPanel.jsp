<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('SystemSendMsgForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id', 			type: 'int'},
    	{name: 'content', 		type: 'string'},
    	{name: 'messageType', 	type: 'string'},
    	{name: 'objectId', 		type: 'string'},
    	{name: 'createTime', 	type: 'string'},
    	{name: 'title', 		type: 'string'},
    	{name: 'imageUrl', 		type: 'string'},
    	{name: 'channelName', 	type: 'string'},
    	{name: 'pageName', 		type: 'string'},
    	{name: 'objectType', 	type: 'string'}
	],
    idProperty: 'id'
});

Ext.define('MyExt.systemSendMsg.SystemSendMsgFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	labelWidth: 55,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
	requires : [ 'MyExt.productManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
	initComponent : function(config) {
		Ext.apply(this, config);
	    this.channelStore = Ext.create('Ext.data.Store', {
	    	autoLoad: true,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'InitModel', 
	    	proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/getComboChannelList.json"/>',
				reader: {
					type : 'json',
                	root: 'data'
            	}
			}
        });
		 
	    this.pageStore = Ext.create('Ext.data.Store', {
		    sortOnLoad: true,
			remoteSort: true,
			model: 'InitModel',
	    	proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/getComboPageListByChannelId.json"/>',
				reader: {
					type : 'json',
                	root: 'data'
            	}
			}
        });
        
         this.messageTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="system.sendmsg.type.notice"/>'},
				{id: '2', name: '<fmt:message key="system.sendmsg.type.activity"/>'}
        	]
        });
        
        this.objectTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '1', name: '<fmt:message key="user.level1"/>'},
				{id: '2', name: '<fmt:message key="user.level2"/>'},
				{id: '3', name: '<fmt:message key="user.pushLevel1"/>'},
				{id: '4', name: '<fmt:message key="user.pushLevel2"/>'}
				
        	]
        });
        
         this.rendererjumptypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="home.popup.jump.type0"/>'},
        		{id: 1, name: '<fmt:message key="home.popup.jump.type1"/>'},
        		{id: 3, name: '<fmt:message key="home.popup.jump.type3"/>'},
        	]
       	});
        
      	this.items = [
			{
				xtype: 'combobox',
				width :100,
				value : '2' ,
	           	fieldLabel: '<fmt:message key="system.sendmsg.check.channel" />',
				name: 'messageType',
				displayField: 'name',
		        valueField: 'id',
	           	queryMode: 'local',
		        store: this.messageTypeStore,
		        anchor: '100%',
		        listeners:{
					select :function(a,b,c){
						channelId = b.data.id;
						var contObj = Ext.ComponentQuery.query('container[name="activityContent"]')[0];
						var typeObj = Ext.ComponentQuery.query('textfield[name="typeName"]')[0];
						var iamgeObj = Ext.ComponentQuery.query('imagefieldset[name="smsImage"]')[0];
						var activityChanel = Ext.ComponentQuery.query('container[name="activityChanel"]')[0];
						var noticeContentObj =  Ext.ComponentQuery.query('textareafield[name="noticeContent"]')[0];
						var inviteObj = Ext.ComponentQuery.query('checkbox[name="isInvitePage"]')[0];
						var beginTimeObj = Ext.ComponentQuery.query('datefield[name="beginTime"]')[0];
						var endTimeObj = Ext.ComponentQuery.query('datefield[name="endTime"]')[0];
						if (channelId == "1"){
							contObj.hide();
							typeObj.hide();
							iamgeObj.hide();
							inviteObj.hide();
							beginTimeObj.hide();
							endTimeObj.hide();
							<!-- activityChanel.hide(); -->
							noticeContentObj.show();
						}if (channelId == "2"){
							contObj.show();
							typeObj.show();
							iamgeObj.show();
							inviteObj.show();
							beginTimeObj.show();
							endTimeObj.show();
							<!-- activityChanel.show(); -->
							noticeContentObj.hide();
						}
					}
		  		}
	       	},{
				fieldLabel: '<fmt:message key="system.sendmsg.isDelaySend" />',
	   			name: 'isDelaySend',
	   			labelWidth: 100,
	   			xtype: 'checkbox',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
		   },{
			xtype: 'combobox',
	    	fieldLabel: '<fmt:message key="home.popup.jump.type" />',
	    	labelWidth: 100,
			name: 'jumpPageType',
         	allowBlank: false,
         	anchor: '100%',
         	displayField: 'name',
			valueField: 'id',
         	store: this.rendererjumptypeStore,
         	listeners:{  
						scope: this,
				      	select:function(combo, record, index){
				      	     var webUrlObj = Ext.ComponentQuery.query('combobox[name="pageConfigId"]')[0];
				      	    if(record.data.id == '3'){
				      	     webUrlObj.show();
				         	 this.selectWareHouseCombobox(combo, record, index);
				      	    }else{
				      	    webUrlObj.hide();
				      	    }
				      	}
		    }
		   },{
        		xtype: 'textfield',
        		padding :'5 5 0' ,
        		width :350,
				anchor: '100%',
        		fieldLabel: '<fmt:message key="system.sendmsg.webUrl"/>',
        		name: 'webUrl'
			},{
	         	xtype: 'combobox',
				labelWidth: 100,
				fieldLabel: '<fmt:message key="system.send.msg.pageConfigId" />',
				name: 'pageConfigId',
		        displayField: 'name',
		        valueField: 'id',
		        store: Ext.create('Ext.data.Store', {
   						model: 'InitModel',
   						data: []
   					}),
				editable: false,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        triggerAction:'all',
		        anchor: '100%',
			},{
	       		xtype: 'container',
	       		padding :'5 5 0' ,
  				layout: 'hbox',
  				items: [{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.beginTime"/>',
				    padding :'5 5 0' ,
				    format: 'Y-m-d H:i:s',
				    readOnly: false,
				    name: 'beginTime',
				    value:Ext.Date.add(new Date(),Ext.Date.DAY,-1),
				    anchor:'100%'
				},{
				    xtype:'datefield',
				    fieldLabel: '<fmt:message key="order.endTime"/>',
				    padding :'5 5 0' ,
				    readOnly: false,
				    format: 'Y-m-d H:i:s',
				    name: 'endTime',
				    value:new Date(),
				    anchor:'100%'
				}]
	       	},{
        		xtype: 'textfield',
        		padding :'5 5 0' ,
        		width :350,
				anchor: '100%',
        		fieldLabel: '<fmt:message key="system.sendmsg.typeName"/>',
        		name: 'typeName'
			},{
	       		xtype: 'container',
	       		padding :'5 5 0' ,
	       		name : 'activityContent',
  				layout: 'hbox',
  				items: [{
		        	xtype: 'textarea',
		        	margin: '5 5',
		        	width :600,
					anchor: '100%',
		        	fieldLabel: '<fmt:message key="system.sendmsg.content"/>',
		        	name: 'content'
  				}]
	       	},{
	       		xtype: 'container',
	       		padding :'5 5 0' ,
	       		name : 'activityContent',
  				layout: 'hbox',
  				items: [{
  					margin: '5 5',
  					width :600,
		        	xtype: 'textarea',
					anchor: '100%',
		        	fieldLabel: '<fmt:message key="system.sendmsg.title"/>',
		        	name: 'title'
  				}]
	       	},{
				xtype: 'imagefieldset',
				name: 'smsImage' ,
				title: '<fmt:message key="system.sendmsg.image"/>',
				collapsible: false,
				anchor: '98%',
				items: [{
					xtype: 'imagepanel',
					combineErrors: true,
					msgTarget: 'under',
					hideLabel: true,
					height: this.clientHeight,
					viewHeight: this.clientHeight
				}]
			},{
				xtype: 'textareafield',
	           	fieldLabel: '<fmt:message key="system.sendmsg.content" />',
	           	name : 'noticeContent',
				name: 'noticeContent',
				maxLength: 100,
				rows : 5,
				hidden : true,
	           	anchor: '98%'
	       	},{
	        		width: 250,
	        		labelWidth: 70,
					xtype: 'checkboxgroup',
					fieldLabel: '<fmt:message key="coupon.sender" />',
			        displayField: 'name',
			        valueField: 'id',
			        style: 'padding: 0px 4px',
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%' , 
			        name: 'level',
			        items: [
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType1" />', name: '<fmt:message key="system.send.msg.objectType1" />',inputValue:'1',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType2" />', name: '<fmt:message key="system.send.msg.objectType2" />',inputValue:'2',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType3" />', name: '<fmt:message key="system.send.msg.objectType3" />',inputValue:'3',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType4" />', name: '<fmt:message key="system.send.msg.objectType4" />',inputValue:'4',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType6" />', name: '<fmt:message key="system.send.msg.objectType6" />',inputValue:'6',anchor:'100%'},
                  		{boxLabel: '<fmt:message key="system.send.msg.objectType7" />', name: '<fmt:message key="system.send.msg.objectType7" />',inputValue:'7',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType8" />', name: '<fmt:message key="system.send.msg.objectType8" />',inputValue:'8',anchor:'100%'},
                 		{boxLabel: '<fmt:message key="system.send.msg.objectType9" />', name: '<fmt:message key="system.send.msg.objectType9" />',inputValue:'9',anchor:'100%'},
             		]
				}
        ];

        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'SystemSendMsgForm',
			root: 'data'
		}); 
		this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent();
    },
    
    selectWareHouseCombobox : function(combo, record, index){
    	var webUrlCombobox  = this.down('combobox[name=pageConfigId]');
    	webUrlCombobox.store.removeAll();
    	webUrlCombobox.setValue();
    	Ext.Ajax.request({
        	url: '<c:url value="/webUrl/list.json"/>',
         	method: 'get',
			scope: this,
			params:{type : 1},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.data != null && responseObject.data.length > 0){
       					try{
       						webUrlCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.data.length; i ++){
       							webUrlCombobox.store.insert(i, {
									id: responseObject.data[i].configId,
									name: responseObject.data[i].name
								});
       						}
       					}catch(e){
    					}
       				}
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
    },
    
    onChangeButton : function(fieldset, fieldName){
    	var imagePanel = this.down('imagepanel');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		imagePanel.store.each(function(record) {
			imageFileBatchPanel.store.insert(imageFileBatchPanel.store.getCount(), {
	       		fileId: record.data.fileId,
				fileName: record.data.fileName,
				fileType: record.data.fileType,
				filePath: record.data.filePath,
				fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
	    	});
	    	return;
		}, this);
		
		var buttons = [{
            text: '<fmt:message key="button.insert"/>',
            handler: function(){
                var rowsData = [];    
                var isError = false;
                imageFileBatchPanel.store.each(function(record) {
                    if(record.data.fileState == imageFileBatchPanel.fileList.FILE_STATUS.QUEUED
                            || record.data.fileState == imageFileBatchPanel.fileList.FILE_STATUS.ERROR){
                        isError = true;
                        return;
                    }
                    record.data.input_file = null;
                    rowsData.push(record.data);    
                }, this);
        
                if(imageFileBatchPanel.isLoader == true){
                    showWarnMsg('<fmt:message key="ajax.loading"/>');
                    return;
                }else if(isError == true){
                    showWarnMsg('<fmt:message key="image.noupload.error"/>');
                    return;
                }
               
                imagePanel.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					imagePanel.store.insert(imagePanel.store.getCount(), {
			       		fileId: record.data.fileId,
						fileName: record.data.fileName,
						fileType: record.data.fileType,
						filePath: record.data.filePath,
						fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
			    	});
			    	return;
				}, this);
				popOtherWin.close();
            },
            scope: this
        },{
            text: '<fmt:message key="button.cancel"/>',
            handler : function(){popOtherWin.close();},
            scope: this
        }];
        openOtherWin('<fmt:message key="image.baseImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
    }
});
