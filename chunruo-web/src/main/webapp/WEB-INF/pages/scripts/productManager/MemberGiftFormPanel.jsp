<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.MemberGiftFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.memberGiftForm'],
    requires : ['MyExt.productManager.ImagePanel','MyExt.userManager.ImagePanel', 'Ext.ux.form.ImageFieldSet'],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
	    
	     this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="start.image.phoneType1"/>'},
        		{id: 0, name: '<fmt:message key="start.image.phoneType0"/>'}
        	]
        });
        
         this.wareHouseStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allProductWarehouseLists}">
        		{id: '${map.value.warehouseId}', name: '${map.value.name}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
        
        this.typeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '1', name: '<fmt:message key="member.gift.type1"/>'},
				{id: '2', name: '<fmt:message key="member.gift.type2"/>'},
				{id: '3', name: '<fmt:message key="member.gift.type3"/>'}
				
        	]
        });
        
	    this.items = [{
			xtype: 'hiddenfield', 
			name: 'templateId', 
			value:this.templateId
		},{
			xtype: 'hiddenfield', 
			name: 'giftId', 
		},{
  			xtype: 'combobox',
  			value: '1',
			fieldLabel: '<fmt:message key="member.gift.type"/>',
			name: 'type',
	        displayField: 'name',
	        valueField: 'id',
	        queryMode: 'local',
	        store: this.typeStore,
	        labelWidth: 70,
	        anchor: '95%'
	       	},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.name" />',
	    	labelWidth: 70,
			name: 'name',
         	allowBlank: false,
         	anchor: '95%',
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.price" />',
	    	labelWidth: 70,
			name: 'price',
         	anchor: '95%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.couponIds" />',
	    	labelWidth: 70,
			name: 'couponIds',
         	anchor: '95%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.productCode" />',
	    	labelWidth: 70,
			name: 'productCode',
         	anchor: '95%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.productSku" />',
	    	labelWidth: 70,
			name: 'productSku',
         	anchor: '95%'
		},{
			xtype: 'textfield',
	    	fieldLabel: '<fmt:message key="member.gift.stockNumber" />',
	    	labelWidth: 70,
			name: 'stockNumber',
         	anchor: '95%'
		},{
			xtype: 'combobox',
			labelWidth: 70,
			fieldLabel: '<fmt:message key="member.gift.wareHouseId" />',
			name: 'wareHouseId',
	        displayField: 'name',
	        valueField: 'id',
	        store: this.wareHouseStore,
	        editable: false,
	        queryMode: 'local',
	        typeAhead: true,
	        anchor: '95%'
     	},{
			xtype: 'imagefieldset',
			title: '<fmt:message key="member.gift.imagePath"/>',
			collapsible: false,
			anchor: '95%',
			items: [{
				xtype: 'imagepanel',
				combineErrors: true,
				msgTarget: 'under',
				hideLabel: true,
				height: this.clientHeight,
				viewHeight: this.clientHeight
			}]
		},{
			xtype: 'imagefieldset',
			title: '<fmt:message key="member.gift.detailImagePath"/>',
			collapsible: false,
			anchor: '95%',
			items: [{
				xtype: 'imagepanels',
				combineErrors: true,
				msgTarget: 'under',
				hideLabel: true,
				height: this.clientHeight,
				viewHeight: this.clientHeight
				}]
		}];
        
        this.buttons = [
        <jkd:haveAuthorize access="/memberYears/saveOrUpdateMemberGift.json">
        {
			text: '<fmt:message key="button.save"/>', 
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			scope: this,  
	        handler: function(){
	        	var rowsData = [];
                this.down('imagepanel').store.each(function(record) {
                    record.data.input_file = null;
                    rowsData.push(record.data);    
                }, this);
                
                var rowsDatas = [];    
		       	this.down('imagepanels').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);

	        	var formValues = this.form.getValues();
	        	var giftId = formValues["giftId"];
	        	var name = formValues["name"];
	        	var price = formValues["price"];
	        	var productCode = formValues["productCode"];
	        	var templateId = formValues["templateId"];
	        	var productSku = formValues["productSku"];
	        	var stockNumber = formValues["stockNumber"];
	        	var wareHouseId = formValues["wareHouseId"];
	        	var type = formValues["type"];
	        	var couponIds = formValues["couponIds"];
	        	
	        	if(this.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                this.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/memberYears/saveOrUpdateMemberGift.json"/>',
			                    scope: this,
			                    params:{ templateId:templateId,recodeGridJson: Ext.JSON.encode(rowsData),detailImagePath: Ext.JSON.encode(rowsDatas),
			                    giftId:giftId,name:name,price:price,productCode:productCode,productSku:productSku,stockNumber:stockNumber,wareHouseId:wareHouseId,type:type,couponIds:couponIds},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
                                     //   this.tabPanel.loadData();
		                       		    popWin.close();
		                       		    this.store.loadPage(1);
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    }
			        		})
			        	}
			        }, this)
	        	}
	        }
	    },
	    </jkd:haveAuthorize>
	    {
			text: '<fmt:message key="button.cancel"/>',
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			handler : function(){popWin.close();},
			scope: this
		}];
	    
	    this.on('afterrender', function(){
			var imagefieldsets = this.query('imagefieldset');
            for(var i = 0; i < imagefieldsets.length; i ++){
                if(i == 1){
                  imagefieldsets[i].on('change', this.onChangeButtons, this);
                }else{
                  imagefieldsets[i].on('change', this.onChangeButton, this); 
                }
                
            }
		}, this);
    	this.callParent();
    },
    
    onChangeButtons : function(fieldset, fieldName){
    	var imagePanels = this.down('imagepanels');
        var imageFileBatchPanel = Ext.create('MyExt.productManager.ImageBatchPanel', {
            isLoader: false,
            header: false
        });
        
        imageFileBatchPanel.store.removeAll([true]);
		imagePanels.store.each(function(record) {
			imageFileBatchPanel.store.insert(imageFileBatchPanel.store.getCount(), {
	       		fileId: record.data.fileId,
				fileName: record.data.fileName,
				fileType: record.data.fileType,
				filePath: record.data.filePath,
				fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
	    	});
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
               
                imagePanels.store.removeAll();
				imageFileBatchPanel.store.each(function(record) {
					imagePanels.store.insert(imagePanels.store.getCount(), {
			       		fileId: record.data.fileId,
						fileName: record.data.fileName,
						fileType: record.data.fileType,
						filePath: record.data.filePath,
						fileState: imageFileBatchPanel.fileList.FILE_STATUS.COMPLETE
			    	});
				}, this);
				popOtherWin.close();
            },
            scope: this
        },{
            text: '<fmt:message key="button.cancel"/>',
            handler : function(){popOtherWin.close();},
            scope: this
        }];
        openOtherWin('<fmt:message key="image.baseOneImage.title"/>', imageFileBatchPanel, buttons, 720, 540);
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