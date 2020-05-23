<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.productManager.HotWordFormPanel', {
    extend : 'Ext.Panel',
 	header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
		    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		
        	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.recordType2"/>'},
        		{id: '1', name: '<fmt:message key="user.recordType1"/>'}
        	]
        }
        );
        
        this.renderer= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.payStatus2"/>'},
        		{id: '1', name: '<fmt:message key="user.payStatus1"/>'}
        	]
        }
        );
        
        this.rendererType= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.warehouseType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.warehouseType2"/>'}
        	]
        }
        );
        
         this.typeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				{id: '1', name: '<fmt:message key="tag.model.type1"/>'},
				{id: '2', name: '<fmt:message key="tag.model.type2"/>'},
				{id: '3', name: '<fmt:message key="tag.model.type3"/>'}
				
        	]
        });
        
        
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
    
    
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
            	labelWidth: 60,
                layout: 'anchor',
            	items: [{
				    xtype: 'numberfield',
				    labelWidth: 60,
	       			fieldLabel: '<fmt:message key="product.tag.sort" />',
	       			allowNegative: false, // 不允许负数 
	       			allowDecimals:false,
	           		name: 'sort',
	           		id:'sort',
	           		anchor: '98%',
	           		value:this.sort
				},{
	  			xtype: 'combobox',
	  			labelWidth: 60,
				fieldLabel: '<fmt:message key="tag.model.type"/>',
		        displayField: 'name',
		        valueField: 'id',
		        queryMode: 'local',
		        id:'name',
		        anchor: '98%',
		        store: this.typeStore,
				listeners:{  
							scope: this,
					      	select:function(combo, record, index){
					         	this.selectTagModelCombobox(combo, record, index);
					      	}
					   	}
	       	},{
	         	xtype: 'combobox',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="tag.model.name" />',
				name: 'tagId',
				id:'tagId',
		        displayField: 'name',
		        valueField: 'id',
		        store: Ext.create('Ext.data.Store', {
   						model: 'InitModel',
   						data: []
   					}),
   				allowBlank:false,
		        editable: false,
		        queryMode: 'local',
		        typeAhead: true,
		        triggerAction:'all',
		        anchor: '98%',
			 }]
        	}]
    	});
    	this.add(form);
    },  
     selectTagModelCombobox : function(combo, record, index){
    	var tagCombobox  = this.down('combobox[name=tagId]');
    	tagCombobox.store.removeAll();
    	tagCombobox.setValue();
    	Ext.Ajax.request({
        	url: '<c:url value="/tagModel/getTagModelBytagType.json"/>',
         	method: 'post',
			scope: this,
			params:{tagType: record.data.id},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.tagModelList != null && responseObject.tagModelList.length > 0){
       					try{
       						tagCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.tagModelList.length; i ++){
       							tagCombobox.store.insert(i, {
									id: responseObject.tagModelList[i].tagId,
									name: responseObject.tagModelList[i].name
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
});