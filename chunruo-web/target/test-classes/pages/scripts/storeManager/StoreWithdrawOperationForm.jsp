<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.storeManager.StoreWithdrawOperationForm', {
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
		
		this.expressCodeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allExpressCodeMaps}">
        		{strId: '${map.value.expressCode}', name: '${map.value.companyName}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
        
        this.bankStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="bank" varStatus="status" items="${allBankMaps}" >
				{id: ${bank.value.bankId}, name: '${bank.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
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
	            flex: 1,
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{    
					labelWidth: 30,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="store.withdrawa.mobile"/>',
					name: 'mobile',
					id: 'mobile',
		            allowBlank: false,
		            anchor: '99%',
		            width:280 
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'add',
			        scope: this,
			        handler: function(){
			        	 this.sure(); 
			        } 
	        	}]
        	},{    
            	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{
			         	xtype: 'combobox',
						labelWidth: 30,
						fieldLabel: '<fmt:message key="store.withdrawa.storeName" />',
						name: 'storeId',
				        displayField: 'name',
				        valueField: 'id',
				        store: Ext.create('Ext.data.Store', {
     						model: 'InitModel',
     						data: []
     					}),
     					editable: false,
     					allowBlank:false,
				        editable: false,
				        queryMode: 'local',
				        typeAhead: true,
				        triggerAction:'all',
				        anchor: '99%',
				        width:280,
				        id:'storeId'
					}]
        	},{    
            	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            defaultType: 'textfield', 
	            style: 'padding: 10px 10px 10px;',
            	items: [{
				    xtype:'combobox',
				    fieldLabel: '<fmt:message key="store.withdrawal.bankName"/>',
				    labelWidth: 85,
				    displayField: 'name',
			        valueField: 'id',
				    name: 'bankId',
				    queryMode: 'local',
				    typeAhead: true,
				    allowBlank:false,
				    store: this.bankStore,
				    anchor:'97%',
				    id:'bankId'
				},{
					xtype:'textfield',
					fieldLabel: '<fmt:message key="store.withdrawal.bankCard"/>',
					labelWidth: 85,
					name: 'bankCard',
					anchor: '99%',
					id:'bankCard'
				},{
					xtype:'textfield',
					fieldLabel: '<fmt:message key="store.withdrawal.bankCardUser"/>',
					labelWidth: 85,
					name: 'bankCardUser',
					anchor: '99%',
					id:'bankCardUser'
				}]
        	}]
    	});
    	this.add(form);
    },
    
    sure : function(combo, record, index){
        var mobile = this.down('textfield[name=mobile]').getValue();
    	var storeCombobox  = this.down('combobox[name=storeId]');
    	storeCombobox.store.removeAll();
    	storeCombobox.setValue();
    	Ext.Ajax.request({
        	url: '<c:url value="/storeWithdrawal/getStoreByMobile.json"/>',
         	method: 'post',
			scope: this,
			params:{mobile: mobile},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.storeList != null && responseObject.storeList.length > 0){
       					try{
       						storeCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.storeList.length; i ++){
       							storeCombobox.store.insert(i, {
									id: responseObject.storeList[i].storeId,
									name: responseObject.storeList[i].name
								});
       						}
       						storeCombobox.setValue(responseObject.storeId);
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
