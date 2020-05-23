<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductGiftForm', {
	extend: 'Ext.data.Model',
	idProperty: 'giftId',
    fields: [
    	{name: 'giftId',	        mapping: 'giftId',	         type: 'int'},
		{name: 'productSpecId',	    mapping: 'productSpecId',	 type: 'int'},
		{name: 'headerImage',	    mapping: 'headerImage',      type: 'string'},
		{name: 'productDesc',	    mapping: 'productDesc',	     type: 'string'},
		{name: 'productTags',	    mapping: 'productTags',	     type: 'string'},
		{name: 'productName',	    mapping: 'productName',	     type: 'string'},
		{name: 'yearNumber',	    mapping: 'yearNumber',	     type: 'int'},
		{name: 'createTime',	    mapping: 'createTime',	     type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',	     type: 'string'},
    ]
});
	  
Ext.define('MyExt.productManager.ProductGiftTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductGiftFormPanel', 'MyExt.productManager.ProductMaterialFormPanel'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		this.tabPanelMask = new Ext.LoadMask(this, {msg:"Please wait..."});
		Ext.apply(this, config);
		this.initWidth = this.width;
	    
	    this.tbar = Ext.create('Ext.Toolbar', { 
	   		scope: this,
	        items:[{
	        	text: '<fmt:message key="button.refresh"/>', 
	            iconCls: 'refresh', 	
	        	handler: function(){
	        		this.loadData();
	        	}, 
	        	scope: this
	        },'-',{
	        	text: '<fmt:message key="product.wholesale.productDesc.edit"/>', 
	        	iconCls: 'Arrownwneswse',
	        	handler: this.productInfoHtmlEditor, 
	        	scope: this
	        },'->',{
	        	iconCls: 'tab_open',
	        	handler: function(){
	        		this.productList.hide();
	        		this.setWidth(this.clientWidth);
	        	}, 
	        	scope: this
	        },'-',{
	        	iconCls: 'tab_close',
	        	handler: function(){
	        		this.hide();
	        		this.productList.show();
	        		this.setWidth(this.initWidth);
	        	}, 
	        	scope: this
	        }]
	    });
	    
	    this.tabPanel = Ext.create('Ext.TabPanel', { 
	    	activeTab : 0,
    		enableTabScroll : true,		
			layoutOnTabChange : true,
			tabWidth : 120,
			items:[{
				xtype: 'productGiftForm',
				isEditor: true,
				title: '<fmt:message key="product.detail"/>'
			},{
				xtype: 'component',
				name: 'htmlContent',
		    	title: '<fmt:message key="product.wholesale.productDesc"/>',
		    	html: '',
		    	cls: 'x-rich-media',
	   	 		autoScroll: true,   
	    		padding: 5,
	    		style: {
	        		color: '#FFFFFF',
	        		backgroundColor:'#FFFFFF'
	    		}
	    	}]
		});
		this.items = [this.tabPanel];
    	this.callParent(arguments);
    },
    
    transferData : function(tabPanel, record, clientWidth){
    	this.clientWidth = clientWidth;
    	this.tabPanel = tabPanel;
    	this.record = record;
    	this.loadData();
    },
    
    loadData : function(){
    	this.tabPanel.tabPanelMask.show();
    	this.tabPanel.down('productGiftForm').tabPanel = this.tabPanel;
    	this.tabPanel.down('productGiftForm').loadRecord(Ext.create('ProductGiftForm'));
    	
    	Ext.Ajax.request({
       		url: '<c:url value="/product/getProductGiftInfoById.json"/>',
        	method: 'post',
			scope: this,
			params:{giftId: this.record.data.giftId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				var productGiftForm = this.tabPanel.down('productGiftForm');
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductGiftForm', this.recordData);
       					productGiftForm.loadRecord(this.recordObject);
       					this.tabPanel.down('[name=htmlContent]').update(this.recordData.productDesc);
       				}
       				
       				var imagePanel = productGiftForm.down('[xtype=imagepanel]');
       				imagePanel.store.removeAll();
       				if(responseObject.imageList != null && responseObject.imageList.length > 0){
       					try{
       						imagePanel.store.removeAll();
       						for(var i = 0; i < responseObject.imageList.length; i ++){
       							imagePanel.store.insert(i, {
									fileId: responseObject.imageList[i].fileId,
									fileName: responseObject.imageList[i].fileName,
									fileType: responseObject.imageList[i].fileType,
									filePath: responseObject.imageList[i].filePath,
									fileState: 200
								});
       						}
       					}catch(e){
    					}
       				}
       			}
			}
    	}, this);
    },
    
    productInfoHtmlEditor : function(){
    	var productEdit = Ext.create('MyExt.productManager.ProductEdit', {
	    	style:{'line-height': '22px'},
    		viewer: this.viewer,
    		recordData: this.recordData,
    		closable: true
    	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						var htmlContent = productEdit.ueditor.getContent();
	                	productEdit.form.submit({
	                    	waitMsg: 'Loading...',
	                    	url: '<c:url value="/product/saveProductGiftInfoDesc.json"/>',
	                    	scope: this,
	                    	params:{giftId: productEdit.recordData.giftId, productDesc: htmlContent},
	                    	success: function(form, action) {
	                        	var responseObject = Ext.JSON.decode(action.response.responseText);
	                        	if(responseObject.error == false){
	                       			showSuccMsg(responseObject.message);
	                        		this.loadData();
									popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
	                    	}
	        			})
	        		}
	        	}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(this.recordData.name, productEdit, buttons, 820, 620);
    }
});