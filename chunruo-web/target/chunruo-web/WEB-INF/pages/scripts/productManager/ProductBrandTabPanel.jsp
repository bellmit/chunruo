<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductBrandForm', {
	extend: 'Ext.data.Model',
	idProperty: 'brandId',
    fields: [
    	{name: 'brandId',			mapping: 'brandId',		type: 'int'},
		{name: 'name',				mapping: 'name',		type: 'string'},
		{name: 'shortName',			mapping: 'shortName',	type: 'string'},
		{name: 'image',				mapping: 'image',		type: 'string'},
		{name: 'isHot',				mapping: 'isHot',		type: 'bool'},
		{name: 'countryId',	 		mapping: 'countryId',		type: 'int'},
		{name: 'countryName',	 	mapping: 'countryName',		type: 'string'},
		{name: 'initial',	        mapping: 'initial',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductBrandTabPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['MyExt.productManager.ProductBrandFormPanel'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'bproduct',
	recordData: null,
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
	            scope: this,
	        	handler: function(){
	        		this.loadData();
	        	}
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
				xtype: 'productBrandForm',
				isEditor: true,
				title: '<fmt:message key="productBrand.detail"/>'
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
    	this.tabPanel.down('productBrandForm').tabPanel = this;
    	this.tabPanel.down('productBrandForm').loadRecord(Ext.create('ProductBrandForm'));
    	this.tabPanel.down('productBrandForm').loadRecord(Ext.create('ProductBrandForm'));
                                                    
    	Ext.Ajax.request({
       		url: '<c:url value="/brand/getBrandById.json"/>',
        	method: 'post',
			scope: this,
			params:{brandId: this.record.data.brandId},
         	success: function(response){
         		this.tabPanel.tabPanelMask.hide();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.data != null){
       					this.recordData = responseObject.data;
       					this.recordObject = Ext.create('ProductBrandForm', this.recordData);
       					this.tabPanel.down('productBrandForm').loadRecord(this.recordObject);
       				}

       				    var productBrandFormPanel = this.tabPanel.down('productBrandForm');

	       				var imagePanel = this.tabPanel.down('productBrandForm').down('[xtype=imagepanel]');
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
	       				
	       				var imagePanels = this.tabPanel.down('productBrandForm').down('[xtype=imagepanels]');
	       				imagePanels.store.removeAll();
	       				if(responseObject.countryImageList != null && responseObject.countryImageList.length > 0){
	       					try{
	       						imagePanels.store.removeAll();
	       						for(var i = 0; i < responseObject.countryImageList.length; i ++){
	       							imagePanels.store.insert(i, {
	       						     	fileId: responseObject.countryImageList[i].fileId,
										fileName: responseObject.countryImageList[i].fileName,
										fileType: responseObject.countryImageList[i].fileType,
										filePath: responseObject.countryImageList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
	       				
	       				var adImagePanel = this.tabPanel.down('productBrandForm').down('[xtype=adImagePanel]');
	       				adImagePanel.store.removeAll();
	       				if(responseObject.adImageList != null && responseObject.adImageList.length > 0){
	       					try{
	       						adImagePanel.store.removeAll();
	       						for(var i = 0; i < responseObject.adImageList.length; i ++){
	       							adImagePanel.store.insert(i, {
	       						     	fileId: responseObject.adImageList[i].fileId,
										fileName: responseObject.adImageList[i].fileName,
										fileType: responseObject.adImageList[i].fileType,
										filePath: responseObject.adImageList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
	       				var backGroundImagePanel = this.tabPanel.down('productBrandForm').down('[xtype=backGroundImagePanel]');
	       				backGroundImagePanel.store.removeAll();
	       				if(responseObject.backImageList != null && responseObject.backImageList.length > 0){
	       					try{
	       						backGroundImagePanel.store.removeAll();
	       						for(var i = 0; i < responseObject.backImageList.length; i ++){
	       							backGroundImagePanel.store.insert(i, {
	       						     	fileId: responseObject.backImageList[i].fileId,
										fileName: responseObject.backImageList[i].fileName,
										fileType: responseObject.backImageList[i].fileType,
										filePath: responseObject.backImageList[i].filePath,
										fileState: 200
									});
	       						}
	       					}catch(e){
	    					}
	       				}
	       				
	       				var categoryPickerObj = productBrandFormPanel.down('firstCategoryPicker[name=category]');
	       				var categoryIdObj = productBrandFormPanel.down('hiddenfield[name=categoryId]');
	       				if(responseObject.productCategory != null){
	       				    categoryPickerObj.setRawValue(responseObject.productCategory.name);
	       				    categoryIdObj.setValue(responseObject.productCategory.categoryId);
	       				}else{
	       				   categoryPickerObj.setRawValue('');
	       				   categoryIdObj.setValue('');
	       				}
	       				
	       				
	       				
			        var OBJ = this.tabPanel.down('productBrandForm').down('[xtype=container]');
			        var baseTagContainer = Ext.ComponentQuery.query('container[name="baseTag"]'); //获得组件
			        var obj = Ext.ComponentQuery.query('textfield[name="tagNames"]');
			       	try{
			       			for(var i = 0; i < obj.length; i ++){
			       			//	baseTagContainer[0].remove(obj[i], true);
								obj[i].hide();
								obj[i].destroy();
			       			}
			       		}catch(e){
			 			}
			         var btn1 = Ext.ComponentQuery.query('button[name="btn1"]');
			         var btn2 = Ext.ComponentQuery.query('button[name="btn2"]');
			         try{
			       			for(var i = 0; i < btn1.length; i ++){
								btn1[i].hide();
								btn1[i].destroy();
								btn2[i].hide();
								btn2[i].destroy();
			       			}
			       		}catch(e){
			 			}
				      baseTagContainer[0].hide();
				      baseTagContainer[0].destroy();
					  this.remove(baseTagContainer[0],false);
					  this.remove(OBJ[0],false);
					  this.addItems(true,'',true);
					  if(responseObject.tagModelList != null && responseObject.tagModelList.length > 0){
	       					try{
	       						for(var i = 0; i < responseObject.tagModelList.length; i ++){
	       							this.addItems(false,responseObject.tagModelList[i].name,false);
	       						}
	       					}catch(e){
	    					}
	    		       }
       			   }else{
       				 //showFailMsg(responseObject.message, 4);
       			 }
			}
    	}, this);
    },
    
    addItems : function(isHiddenDelete,value,isLast,isClose){
    	var container = Ext.create('Ext.container.Container', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	name:'contentTagName',
        	items: [{    
            	 xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            name:'baseTag',
            	items: [{
				     xtype: 'textfield',
			    	 fieldLabel: '<fmt:message key="product.tag.name" />',
			    	 labelWidth: 70,
					 name: 'tagNames',
		         	 anchor: '95%',
		         	 value:value
	                },{    
					 hideLabel: true,  
                	 xtype: 'button',
			         iconCls: 'delete',
			         hidden:isHiddenDelete,
			         name:'btn1',
			         scope: this,
			         handler: function(){
			            this.remove(container, false);
			        	container.hide(); 
			        	container.destroy();
			        } 
	        	   },{    
					 hideLabel: true,  
                	 xtype: 'button',
			         iconCls: 'add',
			         scope: this,
			         hidden:!isHiddenDelete,
			         name:'btn2',
			         handler: function(){
			         this.addItems(false);
			        } 
	        	}]
        	}]
    	});
    	if(close){
    	}
    	this.tabPanel.down('productBrandForm').add(container);
    	return container;
    }
});
