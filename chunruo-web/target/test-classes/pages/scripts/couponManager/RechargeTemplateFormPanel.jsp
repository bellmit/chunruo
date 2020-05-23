<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('RechargeTemplateForm', {
	extend: 'Ext.data.Model',
	 idProperty: 'templateId',
    fields: [
    	{name: 'templateId',		mapping: 'templateId',		 type: 'int'},
        {name: 'amount',			mapping: 'amount',		     type: 'string'},
		{name: 'giftAmount',		mapping: 'giftAmount',		 type: 'string'},
		{name: 'giftUserLevel',		mapping: 'giftUserLevel',	 type: 'int'},
		{name: 'giftUserLevelTime',	mapping: 'giftUserLevelTime',type: 'string'},
		{name: 'productId',			mapping: 'productId',		 type: 'int'},
		{name: 'type',			    mapping: 'type',		     type: 'int'},
		{name: 'couponId',			mapping: 'couponId',		 type: 'string'},
		{name: 'giftName',			mapping: 'giftName',		 type: 'string'},
		{name: 'imageUrl',			mapping: 'imageUrl',		 type: 'string'},
		{name: 'isRecommend',		mapping: 'isRecommend',		 type: 'bool'},
		{name: 'userLevel',			mapping: 'userLevel',		 type: 'int'},
		{name: 'createTime',		mapping: 'createTime',		 type: 'string'},
		{name: 'updateTime',		mapping: 'updateTime',		 type: 'int'}

	],
});

Ext.define('MyExt.couponManager.RechargeTemplateFormPanel', {
    alias: ['widget.rechargeTemplateForm'],
    extend : 'Ext.form.Panel',
 	header: false,
 	buttonAlign: 'center',
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
        
         this.typeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="recharge.template.type1"/>'},
				{id: '2', name: '<fmt:message key="recharge.template.type2"/>'},
				{id: '3', name: '<fmt:message key="recharge.template.type3"/>'},
				{id: '4', name: '<fmt:message key="recharge.template.type4"/>'},
				{id: '0', name: '<fmt:message key="recharge.template.type0"/>'},
        	]
        });
        
        this.giftUserLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="recharge.template.giftUserLevel2"/>'},
				{id: '4', name: '<fmt:message key="recharge.template.giftUserLevel4"/>'},
				{id: '5', name: '<fmt:message key="recharge.template.giftUserLevel5"/>'},
				{id: '0', name: '<fmt:message key="recharge.template.giftUserLevel.none"/>'}
        	]
        });
        
        this.userLevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="recharge.template.userLevel1"/>'},
				{id: '2', name: '<fmt:message key="recharge.template.userLevel2"/>'},
				{id: '4', name: '<fmt:message key="recharge.template.userLevel4"/>'},
				{id: '5', name: '<fmt:message key="recharge.template.userLevel5"/>'},
				{id: '6', name: '<fmt:message key="recharge.template.userLevel6"/>'},
				{id: '7', name: '<fmt:message key="recharge.template.userLevel7"/>'}
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
        
      	this.items = [
      	{
			xtype: 'hiddenfield', 
			name: 'templateId', 
		},
      	   {
				fieldLabel: '<fmt:message key="recharge.template.amount" />',
	   			name: 'amount',
	   			labelWidth: 100,
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
		   },
			{
				xtype: 'combobox',
				width :100,
				value : '0' ,
	           	fieldLabel: '<fmt:message key="recharge.template.type" />',
				name: 'type',
				displayField: 'name',
		        valueField: 'id',
	           	queryMode: 'local',
		        store: this.typeStore,
		        padding :'5 5 0' ,
		        anchor: '100%',
		        hehai:this.isEditor,
		        listeners:{
					select :function(a,b,c){
					    var type = b.data.id;
					    if(a.hehai){
					    var couponObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[0];
						var giftNameObj = Ext.ComponentQuery.query('textfield[name="giftName"]')[0];
						var giftAmountObj = Ext.ComponentQuery.query('textfield[name="giftAmount"]')[0];
						var giftUserLevelObj = Ext.ComponentQuery.query('combobox[name="giftUserLevel"]')[0];
					    var giftUserLevelTimeObj = Ext.ComponentQuery.query('textfield[name="giftUserLevelTime"]')[0];
						var productIdObj = Ext.ComponentQuery.query('textfield[name="productId"]')[0];
					    }else{
					    var couponObj = Ext.ComponentQuery.query('textfield[name="couponId"]')[1];
						var giftNameObj = Ext.ComponentQuery.query('textfield[name="giftName"]')[1];
						var giftAmountObj = Ext.ComponentQuery.query('textfield[name="giftAmount"]')[1];
						var giftUserLevelObj = Ext.ComponentQuery.query('combobox[name="giftUserLevel"]')[1];
					    var giftUserLevelTimeObj = Ext.ComponentQuery.query('textfield[name="giftUserLevelTime"]')[1];
						var productIdObj = Ext.ComponentQuery.query('textfield[name="productId"]')[1];
					    }
						
						
						
					    if(type == "1"){
						    couponObj.show();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else if(type == "2"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.show();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else if(type == "3"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.show();
							giftUserLevelTimeObj.show();
						}else if(type == "4"){
						    couponObj.hide();
						    giftNameObj.show();
						    productIdObj.show();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}else {
							couponObj.hide();
							giftNameObj.hide();
							productIdObj.hide();
							giftAmountObj.hide();
							giftUserLevelObj.hide();
							giftUserLevelTimeObj.hide();
						}
					}
		  		}
	       	},{
				fieldLabel: '<fmt:message key="recharge.template.couponId" />',
	   			name: 'couponId',
	   			labelWidth: 100,
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
	  			hidden:true
		   },{
				fieldLabel: '<fmt:message key="recharge.template.giftAmount" />',
	   			name: 'giftAmount',
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
	  			hidden:true
		   },{
				xtype: 'combobox',
				width :100,
				value : '0' ,
	           	fieldLabel: '<fmt:message key="recharge.template.giftUserLevel" />',
				name: 'giftUserLevel',
				displayField: 'name',
		        valueField: 'id',
	           	queryMode: 'local',
	           	padding :'5 5 0' ,
		        store: this.giftUserLevelStore,
		        anchor: '100%',
		        hidden:true
	       	},
	       	{
				fieldLabel: '<fmt:message key="recharge.template.giftUserLevelTime" />',
	   			name: 'giftUserLevelTime',
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
	  			hidden:true
		   },{
				fieldLabel: '<fmt:message key="recharge.template.productId" />',
	   			name: 'productId',
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
	  			hidden:true
		   },{
				fieldLabel: '<fmt:message key="recharge.template.giftName" />',
	   			name: 'giftName',
	   			xtype: 'textfield',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
	  			hidden:true
		   },{
				fieldLabel: '<fmt:message key="recharge.template.isRecommend" />',
	   			name: 'isRecommend',
	   			xtype: 'checkbox',
	   			padding :'5 5 0' ,
	  			anchor: '100%',
		   },{
				xtype: 'combobox',
				value : '1' ,
	           	fieldLabel: '<fmt:message key="recharge.template.userLevel" />',
				name: 'userLevel',
				displayField: 'name',
		        valueField: 'id',
	           	queryMode: 'local',
	           	padding :'5 5 0' ,
		        store: this.userLevelStore,
		        anchor: '100%'
	       	},{
				xtype: 'imagefieldset',
				name: 'imageUrl' ,
				title: '<fmt:message key="recharge.template.imageUrl"/>',
				collapsible: false,
				anchor: '100%',
				items: [{
					xtype: 'imagepanel',
					combineErrors: true,
					msgTarget: 'under',
					hideLabel: true,
					height: this.clientHeight,
					viewHeight: this.clientHeight
				}]
			}
        ];

        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'RechargeTemplateForm',
			root: 'data'
		}); 
		if(this.isEditor){
	       	this.buttons = [{
				text: '<fmt:message key="button.save"/>', 
				style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
				scope: this,  
	        	handler: this.saveRechargeTemplate
	    	}];
        }
		this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                imagefieldsets[i].on('change', this.onChangeButton, this);
            }
		}, this);
    	this.callParent();
    },
    
     saveRechargeTemplate : function(){
               var rowsData = [];    
       	this.down('imagepanel').store.each(function(record) {
       		record.data.input_file = null;
            rowsData.push(record.data);    
      	}, this);
    	if(this.form.isValid()){
			Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
				if(e == 'yes'){
             		this.form.submit({
                 		waitMsg: 'Loading...',
                 		url: '<c:url value="/rechargeTemplate/saveRechargeTemplate.json"/>',
               			scope: this,
               			params:{recodeGridJson: Ext.JSON.encode(rowsData)},
               			success: function(form, action) {
                   			var responseObject = Ext.JSON.decode(action.response.responseText);
                   			if(responseObject.error == false){
                  				showSuccMsg(responseObject.message);
                  				this.tabPanel.loadData();
							}else{
								showFailMsg(responseObject.message, 4);
							}
               			}
   					})
   				}
   			}, this)
 		}
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
