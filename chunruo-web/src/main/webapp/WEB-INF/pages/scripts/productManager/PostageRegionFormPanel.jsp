<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PostageRegionForm', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'areaId',		mapping: 'areaId',			type: 'int'},
		{name: 'area',	 		mapping: 'area',			type: 'string'},
		{name: 'firstWeigth',	mapping: 'firstWeigth',		type: 'string'},
		{name: 'firstPrice',	mapping: 'firstPrice',		type: 'string'},
		{name: 'afterWeigth',	mapping: 'afterWeigth',		type: 'int'},
		{name: 'afterPrice',	mapping: 'afterPrice',		type: 'string'},
		{name: 'packageWeigth',	mapping: 'packageWeigth',	type: 'int'},
		
    ],
    idProperty: 'areaId'
});

Ext.define('MyExt.productManager.PostageRegionFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
	
	initComponent : function(config) {
		Ext.apply(this, config);
		
       	this.items = [{
           	fieldLabel: '<fmt:message key="postage.template.templateName" />',
			name: 'templateName',
           	labelWidth: 60,
           	readOnly: true,
          	allowBlank: false,
         	anchor: '98%'
       },{
         	fieldLabel: (this.productType == 3 || this.productType == 4) ? '<fmt:message key="postage.template.firstWeigth2" />' : '<fmt:message key="postage.template.firstWeigth" />',
			name: 'firstWeigth',
    		labelWidth: 60,
        	allowBlank: false,
        	anchor: '98%'
  		},{
      		fieldLabel: (this.productType == 3 || this.productType == 4) ? '<fmt:message key="postage.template.firstPrice2" />' : '<fmt:message key="postage.template.firstPrice" />',
			name: 'firstPrice',
         	labelWidth: 60,
          	allowBlank: false,
         	anchor: '98%'
      	},{
           	fieldLabel: (this.productType == 3 || this.productType == 4) ? '<fmt:message key="postage.template.afterWeigth2" />' :'<fmt:message key="postage.template.afterWeigth" />',
			name: 'afterWeigth',
    		labelWidth: 60,
        	allowBlank: false,
        	anchor: '98%'
    	},{
      		fieldLabel: (this.productType == 3 || this.productType == 4) ? '<fmt:message key="postage.template.afterPrice2" />' :'<fmt:message key="postage.template.afterPrice" />',
			name: 'afterPrice',
          	labelWidth: 60,
      		allowBlank: false,
     		anchor: '98%'
   		},{
      		fieldLabel: '<fmt:message key="postage.template.packageWeigth" />',
			name: 'packageWeigth',
          	labelWidth: 60,
     		anchor: '98%'
   		},{
         	xtype: 'itemselector',
            hideLabel: true,
            name: 'areaIds',
           	store: Ext.create('Ext.data.Store', {
           		model: 'InitModel'
        	}),
          	displayField: 'name',
         	valueField: 'id',
            msgTarget: 'side',
            fromTitle: '<fmt:message key="postage.template.allSelect" />',
            toTitle: '<fmt:message key="postage.template.invertSelect" />',
            anchor: '98%',
            height: 280,
            hidden: this.isHideRegion
		}];      
     
        this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'PostageRegionForm',
			root: 'data',
		}); 
    	this.callParent();
    	
    	this.on('actioncomplete', function(form, action, eOpts){
    		var responseObject = Ext.JSON.decode(action.response.responseText);
    		if(responseObject.allAreaList != null && responseObject.allAreaList.length > 0){
    			var store = Ext.create('Ext.data.Store', {model: 'InitModel'});
    			for(var i = 0; i < responseObject.allAreaList.length; i++){
    				var model = Ext.create('InitModel',{
    					id: responseObject.allAreaList[i].areaId,
    					name: responseObject.allAreaList[i].shortName
    				});
					store.add(model);
    			}	
    			this.down('itemselector').setStore(store);
    			
    			if(responseObject.selectAreaIdList != null && responseObject.selectAreaIdList.length > 0){
    				this.down('itemselector').setValue(responseObject.selectAreaIdList);
    			}
			}
    	}, this);
    }
});
