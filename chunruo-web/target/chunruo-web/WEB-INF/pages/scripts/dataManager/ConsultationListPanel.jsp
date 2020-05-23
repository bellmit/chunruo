<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Consultation', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',	 		mapping: 'recordId',		type: 'int'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'productId',			mapping: 'productId',		type: 'int'},
		{name: 'price',	 	        mapping: 'price',	        type: 'string'},
		{name: 'nickName',	 	    mapping: 'nickName',	    type: 'string'},
		{name: 'isSeckillProduct',	mapping: 'isSeckillProduct',type: 'bool'},
		{name: 'number',            mapping: 'number',	        type: 'int'},
		{name: 'mobile',	 	    mapping: 'mobile',	        type: 'string'},
		{name: 'remarks',	 	    mapping: 'remarks',	        type: 'string'},
		{name: 'level',	 	        mapping: 'level',	        type: 'int'},
		{name: 'productName',	 	mapping: 'productName',	    type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.dataManager.ConsultationListPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','MyExt.DateSelectorPicker'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
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
			model: 'Consultation',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/product/consultationList.json"/>',
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
      
		this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: 5, name: '<fmt:message key="user.level5"/>'},
        	    {id: 4, name: '<fmt:message key="user.level4"/>'},
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
        		{id: 0, name: '<fmt:message key="user.level0"/>'}
        	]
        });
       
      	this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="user.recharge.status1"/>'},
        		{id: 2, name: '<fmt:message key="user.recharge.status2"/>'},
        		{id: 3, name: '<fmt:message key="user.recharge.status3"/>'},
        		{id: 4, name: '<fmt:message key="user.recharge.status4"/>'}
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="product.consultation.recordId"/>', dataIndex: 'recordId', width: 60, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.level"/>', dataIndex: 'level', width: 80, sortable : true, locked: true,
        		renderer : this.rendererlevelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererlevelStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="product.consultation.userId"/>', dataIndex: 'userId', width:60,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.mobile"/>', dataIndex: 'mobile', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.nickName"/>', dataIndex: 'nickName', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.productId"/>', dataIndex: 'productId', width: 80,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.productName"/>', dataIndex: 'productName', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.price"/>', dataIndex: 'price', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.number"/>', dataIndex: 'number', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.isSeckillProduct"/>', dataIndex: 'isSeckillProduct', width: 90, sortable : true,locked: true,
        		filter: {xtype: 'textfield'},
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					var str =  "";
					if(val){
						str = '<b><fmt:message key="public.yes"/></b>';
					}else{
						str = '<fmt:message key="public.no"/>';
					}
					return str;
				}
        	},
        	{text: '<fmt:message key="product.consultation.remarks"/>', dataIndex: 'remarks', width: 160, sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.createTime"/>', dataIndex: 'createTime', width: 160, sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.consultation.updateTime"/>', dataIndex: 'updateTime', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	}
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
		this.refundAllListBbar = this.pagingToolbar; 
		
    	this.refundAllList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.refundAllListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	     this.tbar = [
	     <jkd:haveAuthorize access="/product/conRemarks.json">
	     {
        	text: '<fmt:message key="product.consultation.remarks"/>', 
        	iconCls: 'app_manager', 	
        	handler: this.remarks, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/getConsultateNumber.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.edit.consultateNumber"/>', 
        	iconCls: 'add', 	
        	handler: this.editConsultateNumber, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/exportConsultation.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="product.consultation.xls"/>',
        	iconCls: 'excel', 	
        	handler: this.export,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
	    
    	this.items = [this.refundAllList];	
    	this.callParent(arguments);
    	
    	this.gsm = this.refundAllList.getSelectionModel();
    	<jkd:haveAuthorize access="/product/consultationList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.refundAllList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    exportRefund:function(){	
    	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
			if(e == 'yes'){
				window.location.href = "/refund/exportRefund.json";
              	popWin.close();
        	}
        }, this)
	}, 
	
	saveUserRecharge : function(){
    	var userRechargeFormPanel = Ext.create('MyExt.rechargeManager.UserRechargeFormPanel', {
			id: 'userRechargeFormPanel@' + this.id,
    		viewer: this.viewer,
    		isSave: true
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	//var rowsDatas = couponFormPanel.down('selectType').getValue();  
		    	if(userRechargeFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		userRechargeFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/userRecharge/saveUserRecharge.json"/>',
		               			scope: this,
		               			//params:{recodeGridJson: Ext.JSON.encode(rowsDatas)},
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
      	openWin('<fmt:message key="coupon.add"/>', userRechargeFormPanel, buttons, 800, 500);
    },
    
    export : function(){
		var excelPanel = Ext.create('MyExt.dataManager.ExportExcel', {id: 'ExportExcel@ExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.exporter.xls"/>',
			handler: function(){
	            if(excelPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
						if(e == 'yes'){
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
					        window.location.href  = "<c:url value="/product/exportConsultation.json"/>?beginTime="+begin+"&endTime="+end;
			                popWin.close();
						
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
		openWin('<fmt:message key="button.exporter.xls"/>', excelPanel, buttons, 400, 175);
	},
	
	remarks : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		
		if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
		var textAreaMsg = Ext.create('Ext.form.TextArea', {
 			fieldLabel: '<fmt:message key="product.consultation.remarks"/>',
 			labelAlign: 'top',
    		allowBlank: false,
       		anchor: '100%'
 		});	
	 	
	 	var buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			scope: this,  
	        handler: function(){
				if(textAreaMsg.getValue() == null || textAreaMsg.getValue().length == 0){ 
					showWarnMsg('<fmt:message key="product.consultation.remarks"/>', 8);
					return;
				}
				
		     	Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/product/conRemarks.json"/>',
				         	method: 'post',
							scope: this,
							params:{recordId: records[0].data.recordId,remarks: textAreaMsg.getValue()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.error == false){
		                        	popFormWin.close();
		                       		showSuccMsg(responseObject.message);
		                        	this.store.loadPage(1);
		                        	this.gsm.deselectAll();
								}else{
									this.show();
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
	      	}
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];  
 		openFormWin('<fmt:message key="ajax.waitTitle"/>', [textAreaMsg], buttons, 300, 170);
    },
    
    editConsultateNumber:function(){	
		var consultationForm = Ext.create('MyExt.dataManager.ConsultationForm', {id: 'consultationForm@consultationForm', title: '<fmt:message key="button.add"/>'});
		Ext.Ajax.request({
        	url: '<c:url value="/product/getConsultateNumber.json"/>',
         	method: 'post',
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			var consultateNumber = Ext.getCmp('consultateNumber');
					consultateNumber.setValue(responseObject.data.consultateNumber);
        		}
			}
     	})
		
		var buttons = [
		<jkd:haveAuthorize access="/product/editConsultateNumber.json">
		{
			text: '<fmt:message key="button.user.edit.consultate.number"/>',
			handler: function(){
	            if(consultationForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="button.user.edit.confirm"/>', function(e){
						if(e == 'yes'){
							var consultateNumber = Ext.getCmp('consultateNumber').getValue();
							Ext.Ajax.request({
					        	url: '<c:url value="/product/editConsultateNumber.json"/>',
					         	method: 'post',
								scope: this,
								params:{consultateNumber: consultateNumber},
					          	success: function(xresponse){
							    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
			          				if (xresponseObject.success == true){
			          					showSuccMsg(xresponseObject.message);
			          					popWin.close();
			          					this.loadData();
			          				}else{
			          					showFailMsg(xresponseObject.message, 4);
			          				}
								}
					     	})
			        	}
			        }, this)
	        	}
			},
			scope: this
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.user.saleAmount"/>', consultationForm, buttons, 300,150);
	}, 
    
	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="user.recharge.status1"/></b>';
		}else if(val == 2 ){
			str = '<b><fmt:message key="user.recharge.status2"/></b>';
		}else if(val == 3){
			str = '<b><fmt:message key="user.recharge.status3"/></b>';
		}else if(val == 4){
			str = '<b><fmt:message key="user.recharge.status4"/></b>';
		}
		return str;
	},
	
	rendererReceipt : function(val){
		var str =  "";
		if(val){
			str = '<b><fmt:message key="order.refund.receipt"/></b>';
		}
		return str;
	},
	
	 rendererlevelStuts : function(val){
	     if(val == 1) {
            return '<b><fmt:message key="user.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="user.level2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="user.level3"/></b>';
        }else if(val == 4){
            return '<b><fmt:message key="user.level4"/></b>';
        }else if(val == 5){
            return '<b><fmt:message key="user.level5"/></b>';
        }else{
           return '<b><fmt:message key="user.level0"/></b>';
        }
	},
	
	rendererRefundType : function(val){
		var str =  "";
		if(val == '1'){
			str = '<b><fmt:message key="order.refund.refundType.1"/></b>';
		}else{
			str = '<b><fmt:message key="order.refund.refundType.2"/></b>';
		}
		return str;
	}
});