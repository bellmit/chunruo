<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductSpecModel', {
	extend: 'Ext.data.Model',
	fields: [
	   {name: 'productSpecId', type: 'string'},
	   {name: 'primarySpecId', type: 'string'},
	   {name: 'primarySpecName', type: 'string'},
	   {name: 'secondarySpecId', type: 'string'},
	   {name: 'secondarySpecName', type: 'string'},
	   {name: 'productCode', type: 'string'},
	   {name: 'productSku', type: 'string'},
	   {name: 'priceCost', type: 'string'},
	   {name: 'priceWholesale', type: 'string'},
	   {name: 'v2Price', type: 'string'},
	   {name: 'v3Price', type: 'string'},
	   {name: 'priceRecommend', type: 'string'},
	   {name: 'stockNumber', type: 'int'},
	   {name: 'weigth', type: 'int'},
	]
});

Ext.define('MyExt.productManager.ProductSpecForm', {
    extend : 'Ext.Container',
    alias: ['widget.productSpecForm'],
    requires : ['MyExt.productManager.ProductSpecComboEditor'],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    closable: true,
    style: 'background: #f5f5f5 none repeat scroll 0 0;',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.button = Ext.widget('button', {
            style: 'padding: 1px 10px;height: 28px;margin: 10px;font-size: 18px;background: #0089ff none repeat scroll 0 0;border-color: #0089ff;color: #fff;',
            text: '<fmt:message key="product.spec.add"/>',
            scope: this,
            handler: function(){
            	this.addDynamicItemPanel(false);
            }
       	});
	
    	this.productSpecAddButton = Ext.create('Ext.Panel', {
    		border: false,
			region: 'center',
			header: false,
			collapsible: true,
		 	hideLabel: true,
		    scope: this,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
			items:[this.button,{
				xtype : 'label',
				style: 'display:inline;top: 15px;',
            	text: '<fmt:message key="product.spec.add.info"/>'
			}]
    	}); 
		
		this.dynamicItemPanel = Ext.create('Ext.Panel', {
			border: false,
			region: 'center',
			header: false,
			collapsible: true,
		 	hideLabel: true,
		    autoScroll: true,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
    	}); 
    	
    	this.productSpceTpl = new Ext.XTemplate(
			'<div class="more-price-store-table">',
				'<div class="ui-table f12">',
					'<div class="ui-table-head">',
						'<tpl if="this.isShowPrimarySpec()"><div class="ui-table-list width-auto" data-index="0">{[this.showPrimarySpecName()]}</div></tpl>',
						'<tpl if="this.isShowSecondarySpec()"><div class="ui-table-list width-auto" data-index="1">{[this.showSecondarySpecName()]}</div></tpl>',
						'<div class="ui-table-list width-auto" data-index="2"><fmt:message key="product.wholesale.productCode"/></div>',
						'<div class="ui-table-list width-auto" data-index="3"><fmt:message key="product.wholesale.productSku"/></div>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="4"><fmt:message key="product.wholesale.priceCost"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="4"><fmt:message key="product.wholesale.priceCost.dollar"/></div></tpl>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="5"><fmt:message key="product.wholesale.priceWholesale"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="5"><fmt:message key="product.wholesale.priceWholesale.dollar"/></div></tpl>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="6"><fmt:message key="product.wholesale.priceRecommend"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="6"><fmt:message key="product.wholesale.priceRecommend.dollar"/></div></tpl>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="7"><fmt:message key="product.wholesale.v2Price"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="7"><fmt:message key="product.wholesale.v2Price.dollar"/></div></tpl>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="8"><fmt:message key="product.wholesale.v3Price"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="8"><fmt:message key="product.wholesale.v3Price.dollar"/></div></tpl>',
						
						'<div class="ui-table-list width-auto" data-index="9"><fmt:message key="product.wholesale.quantity"/></div>',
						
						'<tpl if="this.productTypeValue()==\'1\'||this.productTypeValue()==\'2\'"><div class="ui-table-list width-auto" data-index="10"><fmt:message key="product.wholesale.weigth"/></div></tpl>',
						'<tpl if="this.productTypeValue()==\'3\'||this.productTypeValue()==\'4\'"><div class="ui-table-list width-auto" data-index="10"><fmt:message key="product.wholesale.weigth.pound"/></div></tpl>',
					'</div>',
					'<div class="ui-table-body">',
						'<tpl for=".">',
						'<div class="ui-table-body-item">',
							'<tpl if="this.isShowPrimarySpec()"><div class="ui-table-list width-auto" data-index="0">',
								'<div style="height:25px;font-weight:bold;" class="page-border-right table-spec-wrap">',
									'<div class="table-spec">{primarySpecName}</div>',
								'</div>',
							'</div></tpl>',
							'<tpl if="this.isShowSecondarySpec()"><div class="ui-table-list width-auto" data-index="1">',
								'<div style="height:25px;" class="page-border-right table-spec-wrap">',
									'<div class="table-spec">{secondarySpecName}</div>',
								'</div>',
							'</div></tpl>',
							'<div class="ui-table-list width-auto" data-index="2">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="code-editable ui-input table-input-price form-mandatory form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{productCode}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="3">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="sku-editable ui-input table-input-price form-mandatory form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{productSku}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="4">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="cost-editable ui-input table-input-store form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{priceCost}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="5">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="price-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{priceWholesale}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="6">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="recom-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{priceRecommend}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="7">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="v2-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{v2Price}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="8">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="v3-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{v3Price}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="9">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="quantity-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{stockNumber}" type="text"></div>',
									'</div>',
								'</div>',
							'</div>',
							'<div class="ui-table-list width-auto" data-index="10">',
								'<div class="table-spec-wrap">',
									'<div class="table-spec page-border-bottom">',
										'<div class="item-con"><input class="weigth-editable ui-input table-input-weight form-mandatory-number" placeholder="<fmt:message key="product.spec.unit.input"/>" value="{weigth}" type="number" min="0" max="100000000"></div>',
									'</div>',
								'</div>',
							'</div>',
						'</div>',
						'</tpl>',
					'</div>',
				'</div>',
			'</div>',
			{
				isPrimarySpec: false,
				isSecondarySpec: false,
				productType: 1,
				primarySpecName: '',
				secondarySpecName: '',
				
				productTypeValue: function(){
					return this.productType;
				},
				
				isShowPrimarySpec: function(){
					return this.isPrimarySpec;
				},
	           	isShowSecondarySpec: function(){
	           		return this.isSecondarySpec;
	           	},
	           	showPrimarySpecName: function(){
	           		return this.primarySpecName;
	           	},
	           	showSecondarySpecName: function(){
	           		return this.secondarySpecName;
	           	}
	       	}
		);
    	
		this.productSpecList = Ext.create('Ext.view.View', {
			hidden: true,
			header: false,
			isShowPrimarySpecSpec: true,
			isShowSecondarySpec: false,
            store: Ext.create('Ext.data.Store', {
	     		autoDestroy: true,
	     		model: 'ProductSpecModel',
	     		data: []
	    	}), 
           	trackOver: true,
           	itemSelector: 'div.ui-table-body-item',
			tpl: this.productSpceTpl,
			scope: this,
           	plugins: [
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'code-editable', dataIndex: 'productCode' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'sku-editable', dataIndex: 'productSku' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'cost-editable', dataIndex: 'priceCost' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'price-editable', dataIndex: 'priceWholesale' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'recom-editable', dataIndex: 'priceRecommend' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'v2-editable', dataIndex: 'v2Price' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'v3-editable', dataIndex: 'v3Price' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'quantity-editable', dataIndex: 'stockNumber' }),
           		Ext.create('Ext.ux.DataView.LabelEditor', {labelSelector: 'weigth-editable', dataIndex: 'weigth' })
           	]
		});
    	
    	this.items = [this.dynamicItemPanel, this.productSpecAddButton, this.productSpecList];
    	this.callParent();
    },
    
    addDynamicItemPanel : function(isEditor){
    	var isSecondarySpec = false;
    	if(this.dynamicItemPanel.items.length > 0){
    		isSecondarySpec = true;
    	}
    	
		var productSpecDataView = Ext.create('Ext.view.View', {
			ttype: 'productSpecDataView',
			isSecondarySpec: isSecondarySpec,
           	store: Ext.create('Ext.data.Store', {
	     		autoDestroy: true,
	     		model: 'InitModel',
	     		data: []
	    	}),
           	tpl: [
           		'<tpl for=".">',
	            	'<div class="thumb-wrap" id="{name:stripTags}">',
	             		'<span class="x-editable">{shortName:htmlEncode}</span>',
	               		'<tpl if="this.isShowImage()">',
	                 		'<div class="thumb"><img src="{filePath}" title="{name:htmlEncode}"></div>',
	                 	'</tpl>',
	              	'</div>',
	            '</tpl>',
	            '<div class="x-clear"></div>',
	            {
	            	isShowImage: function(){
	            		return !productSpecDataView.isSecondarySpec;
	            	}
	            }
           	],
           	selectionModel: {mode: 'MULTI'},
           	trackOver: true,
           	overItemCls: 'x-item-over',
           	itemSelector: 'div.thumb-wrap',
           	emptyText: 'No images to display',
           	plugins: [Ext.create('Ext.ux.DataView.LabelEditor', {
           		dataIndex: 'name',
           		listeners: {
        			scope: this,
        			beforecomplete: function(labelEditor, value, startValue, eOpts){
					    var records = productSpecDataView.store.query('name', value, false, true, true);
					    if(records != null 
					    		&& records.length > 0
					    		&& value != startValue){
					  		showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.spec.unit.name.exist"/>');
					  		labelEditor.setValue(startValue);
						}
						return true;
        			},
        			focusleave: function(labelEditor, value, startValue, eOpts){
        				this.updateProductSpceListRecord();
        			}
        		}
           	})],
           	prepareData: function(data) {
                Ext.apply(data, {
                    shortName: Ext.util.Format.ellipsis(data.name, 15)
                });
                return data;
            },
            listeners: {
        		scope: this,
	            itemdblclick: function(dataView, record, item, index, e, eOpts){
	            	var productSpecFromPanel = Ext.create('Ext.form.Panel', {
					    width: 300,
					    header: false,
					    border: false,
					    labelHidder: true,
					    items: [{
					        xtype: 'filefield',
					        msgTarget: 'side',
					        allowBlank: false,
					        anchor: '100%',
					        labelWidth: 60,
					        fieldLabel: '<fmt:message key="product.spec.unit.image"/>',
					        buttonText: '<fmt:message key="product.spec.unit.image.select"/>'
					    }]
					});
					
					var buttons = [{
						text: '<fmt:message key="button.add"/>',
						handler : function(){
				            if(productSpecFromPanel.isValid()){
				                productSpecFromPanel.submit({
				                    url: '<c:url value="/upload/fileUpload.msp"/>',
				                    waitMsg: 'Uploading your photo...',
				                    scope: this,
				                    success: function(form, action) {
						            	var responseObject = Ext.JSON.decode(action.response.responseText);
						            	if(responseObject.error != null && responseObject.error == false){
						            		record.data.filePath = responseObject.filePath;
						            		productSpecDataView.refresh();
											popFormWin.close();
										}else{
											showFailMsg(responseObject.message, 4);
										}
				                    }
				                });
				            }
						},
						scope: this
					},{
						text: '<fmt:message key="button.cancel"/>',
						handler : function(){popFormWin.close();},
						scope: this
					}];
					openFormWin('<fmt:message key="product.spec.unit.image.edit"/>', productSpecFromPanel, buttons, 320, 95);
	            }
	        }
       	});
       	
       	var productStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				<c:forEach var="map" varStatus="status" items="${allProductSpecNameMaps}" >
				{id: ${map.value.specNameId}, name: '${map.value.specName}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});
	
		var productSpecFieldSet = Ext.create('MyExt.productManager.ProductSpecFieldSet',{
			ttype: 'productSpecFieldSet',
			statusStore: productStore,
			isEditor: isEditor,
			title: '<fmt:message key="product.spec.info"/>',
			deleteTitle: '<fmt:message key="button.delete"/>',
           	anchor: '98%',
           	items: [productSpecDataView,{
				xtype: 'button',
				iconCls: 'add', 	
				style: 'margin-bottom: 10px;margin-left: 2px;',
		    	text: '<fmt:message key="product.spec.element.add" />',
		    	scope: this,
		    	handler: function(){
		    		var specModelPicker = productSpecFieldSet.down('productSpecModelPicker');
		    		if(specModelPicker == null 
		    			|| specModelPicker.getSpecModelValue() == null
		    			|| specModelPicker.getSpecModelValue() == false){
		    			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.spec.unit.type.select"/>');
						return;
		    		}
		    		
		    		var specModelId = specModelPicker.getSpecModelValue();
		    		var uniqueValue = this.generateUUID();
		    		if(!productSpecDataView.isSecondarySpec){
			    		var productSpecFromPanel = Ext.create('Ext.form.Panel', {
						    width: 300,
						    header: false,
						    border: false,
						    labelHidder: true,
						    items: [{
						    	xtype: 'textfield',
			    				fieldLabel: '<fmt:message key="product.spec.unit.name"/>',
						        xtype: 'textfield',
						        labelWidth: 60,
						        allowBlank: false,
						        anchor: '100%'
						    },{
						        xtype: 'filefield',
						        msgTarget: 'side',
						        allowBlank: true,
						        anchor: '100%',
						        labelWidth: 60,
						        fieldLabel: '<fmt:message key="product.spec.unit.image"/>',
						        buttonText: '<fmt:message key="product.spec.unit.image.select"/>'
						    }]
						});
						
						var buttons = [{
							text: '<fmt:message key="button.add"/>',
							handler : function(){
					            if(productSpecFromPanel.isValid()){
					            	var specName = productSpecFromPanel.down('textfield').getValue();
					            	var records = productSpecDataView.store.query('name', specName);
					            	if(records != null && records.length > 0){
					            		showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.spec.unit.name.exist"/>');
										return;
					            	}else{
					            		productSpecFromPanel.submit({
						                    url: '<c:url value="/upload/fileUpload.msp"/>',
						                    waitMsg: 'Uploading your photo...',
						                    scope: this,
						                    success: function(form, action) {
								            	var responseObject = Ext.JSON.decode(action.response.responseText);
								            	if(responseObject.error != null && responseObject.error == false){
								            		productSpecDataView.store.add(Ext.create('InitModel', {name: specName, strId: uniqueValue, specModelId: specModelId, filePath: responseObject.filePath}));
								            		popFormWin.close();
								            		this.updateProductSpceListRecord();
								            	}else{
								            		showFailMsg(responseObject.message, 4);
								            	}
						                    }
						                });
					               	}
					            }
							},
							scope: this
						},{
							text: '<fmt:message key="button.cancel"/>',
							handler : function(){popFormWin.close();},
							scope: this
						}];
						openFormWin('<fmt:message key="product.spec.unit.add"/>', productSpecFromPanel, buttons, 320, 130);
					}else{
						var productSpecFromPanel = Ext.create('Ext.form.Panel', {
						    width: 300,
						    header: false,
						    border: false,
						    labelHidder: true,
						    items: [{
						    	xtype: 'textfield',
			    				fieldLabel: '<fmt:message key="product.spec.unit.name"/>',
						        labelWidth: 60,
						        allowBlank: false,
						        anchor: '100%'
						    }]
						});
						
						var buttons = [{
							text: '<fmt:message key="button.add"/>',
							handler : function(){
					            if(productSpecFromPanel.isValid()){
					            	var specName = productSpecFromPanel.down('textfield').getValue();
					            	var records = productSpecDataView.store.query('name', specName);
					            	if(records != null && records.length > 0){
					            		showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.spec.unit.name.exist"/>');
										return;
					            	}else{
						            	productSpecDataView.store.add(Ext.create('InitModel', {name: specName, strId: uniqueValue, specModelId: specModelId}));
						            	popFormWin.close();
						            	this.updateProductSpceListRecord();
						            	return;
					               	}
					            }
							},
							scope: this
						},{
							text: '<fmt:message key="button.cancel"/>',
							handler : function(){popFormWin.close();},
							scope: this
						}];
						openFormWin('<fmt:message key="product.spec.unit.add"/>', productSpecFromPanel, buttons, 320, 95);
					}
		    	}
			},{
				xtype: 'button',
				iconCls: 'delete', 	
				style: 'margin-bottom: 10px;margin-left: 10px;',
		    	text: '<fmt:message key="product.spec.element.delete" />',
		    	scope: this,
		    	handler: function(){
		    		var rowsData = [];		
					var records = productSpecDataView.getSelection();
					if(records.length == 0){
						showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
						return;
					}	
					
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							for(var i = 0; i < records.length; i++){
								productSpecDataView.store.remove(records[i]);
							}
							this.updateProductSpceListRecord();
						}
					}, this)
		    	}
			},{
				xtype: 'button',
				iconCls: 'deletes', 	
				style: 'margin-bottom: 10px;margin-left: 10px;',
		    	text: '<fmt:message key="product.spec.element.batch.set" />',
		    	scope: this,
		    	handler: function(){
					
					
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							this.updateProductSpceListRecord(1);
						}
					}, this)
		    	}
			}],
			scope: this,
        	listeners: {
        		scope: this,
        		clickButton: function(){
        			Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							this.dynamicItemPanel.remove(productSpecFieldSet, false);
						  	productSpecFieldSet.hide();
						  	productSpecFieldSet.destroy();
						  	
						  	if(this.dynamicItemPanel.items.length >= 2){
					    		this.productSpecAddButton.setHidden(true);
					    	}else{
					    		this.productSpecAddButton.setHidden(false);
					    		if(this.dynamicItemPanel.items.length == 1){
					    			var firstProductSpecFieldSet = this.dynamicItemPanel.down('[ttype=productSpecFieldSet]');
					    			firstProductSpecFieldSet.setProductSpecLabel(false);
					    			firstProductSpecFieldSet.down('[ttype=productSpecDataView]').isSecondarySpec = false;
					    			firstProductSpecFieldSet.down('[ttype=productSpecDataView]').refresh();
					    			this.updateProductSpceListRecord();
					    		}else if(this.dynamicItemPanel.items.length == 0){
					    			this.productSpecList.setHidden(true);
					    			this.updateProductSpceListRecord();
					    		}
					    	}
						}
					}, this)
        		},
        		storeDataChanged : function(picker, record, item, index, e, eOpts){				
    				var isMayDataChanged = true;
        			var productSpecFieldSets = this.dynamicItemPanel.query('[ttype=productSpecFieldSet]');
    				if(productSpecFieldSets != null && productSpecFieldSets.length > 1){
    					for(var i = 0; i < productSpecFieldSets.length; i ++){
    						if(productSpecFieldSets[i].getId() != productSpecFieldSet.getId()){
    							var specModelPicker = productSpecFieldSets[i].down('productSpecModelPicker');
    							if(record.data.specModelId == specModelPicker.getSpecModelValue()){
    								isMayDataChanged = false;
    								showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.spec.type.exist"/>');
    							}
    						}
    					}
    				}
    				
    				if(isMayDataChanged){
    					picker.setSpecModelValue(record.data.specModelId);
    					picker.setValue(record.data.name);
    					if(productSpecDataView.store.getCount() > 0){
    						productSpecDataView.store.each(function(storeRecord) {
    							storeRecord.set('specModelId', record.data.specModelId);   
	    					}, this);
	    					this.updateProductSpceListRecord();
	    				}
    				}
        		}
        	}
    	});
    	
    	this.productSpecList.setHidden(false);
    	productSpecFieldSet.setProductSpecLabel(isSecondarySpec);
    	productSpecFieldSet = this.dynamicItemPanel.add(productSpecFieldSet);
    	if(isSecondarySpec){
    		this.productSpecAddButton.setHidden(true);
    	}else{
    		this.productSpecAddButton.setHidden(false);
    	}
    	
    	return productSpecFieldSet;
    },
    
    updateProductSpceListRecord : function(isBatchSet){
    	var tempProductSpecStore = Ext.create('Ext.data.Store', {
	    	autoDestroy: true,
	     	model: 'ProductSpecModel',
	     	data: []
	    });
	    
			var priceCostSet = '';
			var priceWholesaleSet = '';
			var priceRecommendSet = '';
			var v2PriceSet = '';
			var v3PriceSet = '';
			var quantitySet = '';
			var weigthSet = '';
			var primarySpecNameSet = '';
			var secondarySpecNameSet = '';
			
	    if(this.productSpecList.store.getCount() > 0){
	    	for(var i = 0; i < this.productSpecList.store.getCount(); i ++){
	    	    if(isBatchSet == 1 && i == 0){
	    	       var record = this.productSpecList.store.getAt(i).data;
	    	       priceCostSet = record.priceCost;
	    	       priceWholesaleSet = record.priceWholesale;
	    	       priceRecommendSet = record.priceRecommend;
	    	       v2PriceSet = record.v2Price;
	    	       v3PriceSet = record.v3Price;
	    	       quantitySet = record.quantity;
	    	       weigthSet = record.weigth;
	    	       primarySpecNameSet = record.primarySpecName;
	    	       secondarySpecNameSet = record.secondarySpecName;
	    	    }
	    		tempProductSpecStore.add(this.productSpecList.store.getAt(i));
	    		
	    	}
	    }
    
    	this.productSpecList.store.removeAll();
    	var productSpecFieldSets = this.dynamicItemPanel.query('[ttype=productSpecFieldSet]');
    	if(productSpecFieldSets != null && productSpecFieldSets.length > 0){
    		var primarySpecName;
    		var primarySpecStore;
    		var secondarySpecName;
    		var secondarySpecStore;
    		var isNotEmptyPrimarySpecStore = false;
    		var isNotEmptySecondarySpecStore = false;
    		for(var i = 0; i < productSpecFieldSets.length; i ++){
    			var productSpecPanel = productSpecFieldSets[i].down('[ttype=productSpecDataView]');
        		if(productSpecPanel != null && !productSpecPanel.isSecondarySpec){
        			primarySpecStore = productSpecPanel.store;
        			if(primarySpecStore.getCount() > 0){
        				isNotEmptyPrimarySpecStore = true;
        				
        				var specModelPicker = productSpecFieldSets[i].down('productSpecModelPicker');
		    			if(specModelPicker != null && specModelPicker.getSpecModelValue() != null){
		    				primarySpecName = specModelPicker.getValue();
		    			}
        			}
        		}else if(productSpecPanel != null && productSpecPanel.isSecondarySpec){
        			secondarySpecStore = productSpecPanel.store;
        			if(secondarySpecStore.getCount() > 0){
        				isNotEmptySecondarySpecStore = true;
        				
        				var specModelPicker = productSpecFieldSets[i].down('productSpecModelPicker');
		    			if(specModelPicker != null && specModelPicker.getSpecModelValue() != null){
		    				secondarySpecName = specModelPicker.getValue();
		    			}
        			}
        		}
       		}
       		
       		this.productSpceTpl.isPrimarySpec = false;
       		this.productSpceTpl.isSecondarySpec = false;
       		if(isNotEmptyPrimarySpecStore){
       			this.productSpceTpl.isPrimarySpec = true;
       			this.productSpceTpl.primarySpecName = primarySpecName;
       			if(isNotEmptySecondarySpecStore){
       				this.productSpceTpl.isSecondarySpec = true;
       				this.productSpceTpl.secondarySpecName = secondarySpecName;
       			}
       			
       			
       			this.productSpecList.store.add(Ext.create('ProductSpecModel', {
       		 	                productSpecId: -1, 
			       				primarySpecId: -1,
			       				primarySpecName: '<fmt:message key="product.spec.element.batch"/>',
			       				secondarySpecName: '<fmt:message key="product.spec.element.batch"/>',
			       				priceCost: priceCostSet,
			       				priceWholesale: priceWholesaleSet,
			       				priceRecommend: priceRecommendSet,
			       				v2Price: v2PriceSet,
			       				v3Price: v3PriceSet,
			       				quantity: quantitySet,
			       				weigth: weigthSet,
		       				}));
       			
       			if(isNotEmptySecondarySpecStore){
       				for(var index = 0; index < primarySpecStore.getCount(); index++){
       					for(var isecondary = 0; isecondary < secondarySpecStore.getCount(); isecondary++){
       						var productSpecId = '';
       						var productCode = '';
	       					var productSku = '';
	       					var priceCost = '';
	       					var priceWholesale = '';
	       					var priceRecommend = '';
	       					var v2Price = '';
	       					var v3Price = '';
	       					var quantity = '';
	       					var stockNumber = '';
	       					var weigth = '';
	       					
	       					var primarySpecName = primarySpecStore.getAt(index).data.name;
	       				    var secondarySpecName = secondarySpecStore.getAt(isecondary).data.name;
	       					var primarySpecId = primarySpecStore.getAt(index).data.strId;
	       					var secondarySpecId = secondarySpecStore.getAt(isecondary).data.strId;
	       					if(tempProductSpecStore != null && tempProductSpecStore.getCount() > 0){
	       						var records = tempProductSpecStore.queryBy(function(record) {   
	       							return record.get('primarySpecId') == primarySpecId && record.get('secondarySpecId') == secondarySpecId; 
								}, this);
								
		       					if(records != null && records.length > 0){
		       						productSpecId = records.getAt(0).data.productSpecId;
		       						productCode = records.getAt(0).data.productCode;
		       						productSku = records.getAt(0).data.productSku;
		       						stockNumber = records.getAt(0).data.stockNumber;
		       						
		       						if(isBatchSet == 1){
		       						priceCost = priceCostSet;
		       						priceWholesale = priceWholesaleSet;
		       						priceRecommend = priceRecommendSet;
		       						v2Price = v2PriceSet;
		       						v3Price = v3PriceSet;
		       						weigth = weigthSet;
	       						}else{
		       						priceCost = records.getAt(0).data.priceCost;
		       						priceWholesale = records.getAt(0).data.priceWholesale;
		       						priceRecommend = records.getAt(0).data.priceRecommend;
		       						v2Price = records.getAt(0).data.v2Price;
		       						v3Price = records.getAt(0).data.v3Price;
		       						quantity = records.getAt(0).data.quantity;
		       						weigth = records.getAt(0).data.weigth;
		       						
	       						}
		       					}
	       					}
	       				   
       					
       						this.productSpecList.store.add(Ext.create('ProductSpecModel', {
       							productSpecId: productSpecId, 
			       				primarySpecId: primarySpecId,
			       				primarySpecName: primarySpecName,
			       				secondarySpecId: secondarySpecId,
			       				secondarySpecName: secondarySpecName,
			       				productCode: productCode,
			       				productSku: productSku,
			       				priceCost: priceCost,
			       				priceWholesale: priceWholesale,
			       				priceRecommend: priceRecommend,
			       				v2Price: v2Price,
			       				v3Price: v3Price,
			       				quantity: quantity,
			       				stockNumber: stockNumber,
			       				weigth: weigth,
		       				}));
       					}
       				}
       			}else{
       				for(var index = 0; index < primarySpecStore.getCount() + 1; index++){
       					var productSpecId = '';
	       				var productCode = '';
	       				var productSku = '';
	       				var priceCost = '';
	       				var priceWholesale = '';
	       				var priceRecommend = '';
	       				var v2Price = '';
	       				var v3Price = '';
	       				var stockNumber = '';
	       				var weigth = '';
	       				
       				    var primarySpecName = primarySpecStore.getAt(index).data.name;
    				    var primarySpecId = primarySpecStore.getAt(index).data.strId;
       					if(tempProductSpecStore != null && tempProductSpecStore.getCount() > 0){
       						var records = tempProductSpecStore.query('primarySpecId', primarySpecId, false, true, true);
	       					if(records != null && records.length > 0){
	       						productSpecId = records.getAt(0).data.productSpecId;
	       						productCode = records.getAt(0).data.productCode;
	       						productSku = records.getAt(0).data.productSku;
	       						stockNumber = records.getAt(0).data.stockNumber;
	       						
	       						if(isBatchSet == 1){
		       						priceCost = priceCostSet;
		       						priceWholesale = priceWholesaleSet;
		       						priceRecommend = priceRecommendSet;
		       						v2Price = v2PriceSet;
		       						v3Price = v3PriceSet;
		       						weigth = weigthSet;
	       						}else{
		       						priceCost = records.getAt(0).data.priceCost;
		       						priceWholesale = records.getAt(0).data.priceWholesale;
		       						priceRecommend = records.getAt(0).data.priceRecommend;
		       						v2Price = records.getAt(0).data.v2Price;
		       						v3Price = records.getAt(0).data.v3Price;
		       						weigth = records.getAt(0).data.weigth;
	       						}
	       						
	       					}
       					}
	       					
       					this.productSpecList.store.add(Ext.create('ProductSpecModel', {
			       			productSpecId: productSpecId, 
			       			primarySpecId: primarySpecId,
			       			primarySpecName: primarySpecName,
			       			productCode: productCode,
			       			productSku: productSku,
		       				priceCost: priceCost,
		       				priceWholesale: priceWholesale,
		       				priceRecommend: priceRecommend,
		       				v2Price: v2Price,
		       				v3Price: v3Price,
		       				stockNumber: stockNumber,
		       				weigth: weigth,
		       			}));
       				}
       			}
       		}
    	}
    	this.productSpecList.refresh();
    },
    
    getPrimarySpecRowsData : function(){
    	var primarySpecRowsData = [];
    	var productSpecFieldSets = this.dynamicItemPanel.query('[ttype=productSpecFieldSet]');
    	if(productSpecFieldSets != null && productSpecFieldSets.length > 0){
    		for(var i = 0; i < productSpecFieldSets.length; i ++){
    			var productSpecPanel = productSpecFieldSets[i].down('[ttype=productSpecDataView]');
        		if(productSpecPanel != null && !productSpecPanel.isSecondarySpec){
        			primarySpecStore = productSpecPanel.store;
        			if(primarySpecStore.getCount() > 0){
        				primarySpecStore.each(function(record) {
	   						primarySpecRowsData.push(record.data);    
	    				}, this);
        			}
        		}
       		}
       	}
	    return primarySpecRowsData;
    },
    
    getSecondarySpecRowsData : function(){
    	var secondarySpecRowsData = [];
    	var productSpecFieldSets = this.dynamicItemPanel.query('[ttype=productSpecFieldSet]');
    	if(productSpecFieldSets != null && productSpecFieldSets.length > 0){
    		for(var i = 0; i < productSpecFieldSets.length; i ++){
    			var productSpecPanel = productSpecFieldSets[i].down('[ttype=productSpecDataView]');
        		if(productSpecPanel != null && productSpecPanel.isSecondarySpec){
        			secondarySpecStore = productSpecPanel.store;
        			if(secondarySpecStore.getCount() > 0){
        				secondarySpecStore.each(function(record) {
	   						secondarySpecRowsData.push(record.data);    
	    				}, this);
        			}
        		}
       		}
       	}
	    return secondarySpecRowsData;
    },
    
    getProductSpecRowsData : function(){
    	var productSpecRowsData = [];
    	this.productSpecList.store.each(function(record) {
	   		productSpecRowsData.push(record.data);    
	    }, this);
	    return productSpecRowsData;
    },
    
    generateUUID : function() {
		var d = new Date().getTime();
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		  	var r = (d + Math.random()*16)%16 | 0;
		  	d = Math.floor(d/16);
		  	return (c=='x' ? r : (r&0x3|0x8)).toString(16);
		});
		return 'tmp_' + uuid;
	}
});
