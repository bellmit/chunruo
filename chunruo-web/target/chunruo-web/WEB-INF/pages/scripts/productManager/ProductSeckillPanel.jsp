<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductSeckillPanel', {
	extend: 'Ext.data.Model',
	idProperty: 'seckillId',
    fields: [
		{name: 'seckillId',			mapping: 'seckillId',		type: 'string'},
		{name: 'seckillName',		mapping: 'seckillName',		type: 'string'},
		{name: 'statusName',	    mapping: 'statusName',		type: 'string'},
		{name: 'startTime',			mapping: 'startTime',		type: 'string'},
		{name: 'endTime',	        mapping: 'endTime',			type: 'string'},
		{name: 'status',			mapping: 'status',			type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductSeckillPanel', {
   	extend : 'Ext.panel.Panel',
    closable: true,
	columnLines: true,
	layout: 'border',
	animCollapse: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
    	this.store = Ext.create('Ext.data.Store', {
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductSeckillPanel',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/seckill/seckillList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'startTime',
	            direction: 'asc'
	        }]
		});
		
		this.columns = [
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'seckillId', width: 60, sortable: true},
			{text: '<fmt:message key="productSeckill.seckillName"/>', dataIndex: 'seckillName', width: 120, sortable: true},
        	{text: '<fmt:message key="productSeckill.startTime"/>', dataIndex: 'startTime', width: 110, sortable: true, align: 'center'},
        	{text: '<fmt:message key="productSeckill.endTime"/>', dataIndex: 'endTime', width: 110, sortable: true, align: 'center'},
        	{text: '<fmt:message key="productSeckill.status"/>', dataIndex: 'status', width: 70, sortable : true, align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == false) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	}
        	},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/seckill/saveProductSeckill.json">
        {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addSeckill,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/seckill/deleteSeckillStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteSeckill, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/seckill/setSeckillStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableSeckill, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/seckill/setSeckillStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel',	
        	handler: this.cancelSeckill, 
        	scope: this
        }
        </jkd:haveAuthorize>
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
		
		this.seckillList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			width: 520,
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
		    bbar: this.pagingToolbar,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.ProductSeckillContList', {
	       	seckillList: this.seckillList,
        	viewer: this.viewer,
	        border: false,
	       	region: 'center',
	        header: false
        });
    	
    	this.items = [this.seckillList, this.east];	
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/seckill/seckillList.json">
	    this.store.load(); 
	    </jkd:haveAuthorize>  
	    
    	this.gsm = this.seckillList.getSelectionModel();
	    this.seckillList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/seckill/getProductListBySeckillId.json">
	    	this.east.transferData(record);
	    	</jkd:haveAuthorize>  
	    }, this);
	    
	    
	    <jkd:haveAuthorize access="/seckill/getSeckillById.json">
	    this.menu = Ext.create('Ext.menu.Menu', {
        	items:[{
		       	iconCls: 'Chartpieadd',
				text: '<fmt:message key="button.edit"/>',
				handler: this.editSeckill,
		    	scope: this
		    }]
    	});
    	
	    this.seckillList.on('itemcontextmenu', function(view, record, item, index, e){
	    	e.stopEvent();
    		this.menu.record = record;
	    	this.menu.showAt(e.getXY());	
		}, this);
	    </jkd:haveAuthorize>
    },
    
    addSeckill : function(){
    	var productSeckillFormPanel = Ext.create('MyExt.productManager.ProductSeckillFormPanel', {
			id: 'add@productSeckillFormPanel' + this.id,
    		viewer: this.viewer,
    		edit: false,
   	 	});
   	 	
    	var buttons = [
    	<jkd:haveAuthorize access="/seckill/saveProductSeckill.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(productSeckillFormPanel.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							productSeckillFormPanel.submit({
		                    	url: '<c:url value="/seckill/saveProductSeckill.json"/>',
		                    	waitMsg: '<fmt:message key="ajax.loading"/>',
		                    	scope: this,
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
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.add"/>', productSeckillFormPanel, buttons, 380, 200);
    },     
   	
   	editSeckill : function(){
		var productSeckillFormPanel = Ext.create('MyExt.productManager.ProductSeckillFormPanel', {
			id: 'edit@productSeckillFormPanel' + this.id,
    		viewer: this.viewer,
    		edit: false,
   	 	});
   	 	productSeckillFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/seckill/getSeckillById.json"/>', 
    		params: {seckillId: this.menu.record.data.seckillId}, 
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
   	 	
   	 	var buttons = [{
   	 		scope: this,
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(productSeckillFormPanel.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							productSeckillFormPanel.submit({
		                    	url: '<c:url value="/seckill/saveProductSeckill.json"/>',
		                    	waitMsg: '<fmt:message key="ajax.loading"/>',
		                    	scope: this,
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
			}
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.edit"/>', productSeckillFormPanel, buttons, 380, 200);
   	},
   	
   	deleteSeckill : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.seckillId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/seckill/deleteSeckillStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                        	this.store.loadPage(1);
		                    this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	        }
		}, this)
   	},
   	
   	enableSeckill : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.seckillId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/seckill/setSeckillStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                        	this.store.loadPage(1);
		                    this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	        }
		}, this)
   	},
   	
   	cancelSeckill : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.seckillId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/seckill/setSeckillStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                        	this.store.loadPage(1);
		                    this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	        }
		}, this)
   	}
});