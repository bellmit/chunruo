<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PostageTpl', {
    extend: 'Ext.data.TreeModel',
    fields: [
        {name: 'id',     		mapping: 'id',				type: 'string'},
        {name: 'warehouseId',   mapping: 'warehouseId',  	type: 'int'},
        {name: 'warehouseName', mapping: 'warehouseName',	type: 'string'},
        {name: 'templateId',    mapping: 'templateId', 		type: 'int'},
        {name: 'productType',   mapping: 'productType', 	type: 'int'},
        {name: 'isFreeTemplate',mapping: 'isFreeTemplate',	type: 'string'},
        {name: 'text',     		mapping: 'text',			type: 'string'},
        {name: 'expanded',     	mapping: 'expanded',		type: 'bool'},
        {name: 'leaf',     		mapping: 'leaf',			type: 'bool'}
    ]
});

Ext.define('MyExt.productManager.PostageTemplatePanel', {
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
	
		this.store = Ext.create('Ext.data.TreeStore', {
			model: 'PostageTpl',
        	proxy: {
           	 	type: 'ajax',
            	url: '<c:url value="/postageTpl/getPostageTplTree.js"/>'
        	},
        	root: {
            	text: 'ROOT',
            	expanded: true
        	},
        	sorters: [{
	            property: 'id',
	            direction: 'asc'
	        }]
    	});
    	
		this.menuTreePanel = Ext.create('Ext.TreePanel', {
			warehouseId: 0,
			region: 'west',
	        header: false,
	        width: 380,
        	collapsible: true,
        	columnLines: true,
        	useArrows: true,
        	rootVisible: false,
        	store: this.store,
	        multiSelect: true,
	        columns: [{
	            xtype: 'treecolumn', 
	            columnLines: true,
	            text: '<fmt:message key="postage.template.name"/>',
	            width: 240,
	            sortable: false,
	            dataIndex: 'text',
	            locked: true
	        },{
	            text: '<fmt:message key="postage.template.pinkageAmount"/>',
           	 	width: 70,
            	dataIndex: 'freePostageAmount',
            	sortable: false
	        }],
	        tbar:[
	        <jkd:haveAuthorize access="/postageTpl/getPostageTplTree.js">
	        {
		       	iconCls: 'refresh',
		       	type: 'root',
				text: '<fmt:message key="system.menu.refresh"/>',
				handler: function(){this.store.reload()},
		    	scope: this
		    }
		    </jkd:haveAuthorize>
		    ]
        });
        
        this.menu = Ext.create('Ext.menu.Menu', {
        	items:[
        	<jkd:haveAuthorize access="/postageTpl/createPostageTpl.json">
        	{
		       	iconCls: 'add',
				text: '<fmt:message key="button.add"/>',
				handler: this.addPostageTpl,
		    	scope: this
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/postageTpl/editPostageTemplate.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
		       	iconCls: 'Chartpieadd',
				text: '<fmt:message key="button.edit"/>',
				handler: this.editPostageTpl,
		    	scope: this
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/postageTpl/deletePostageTemplate.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
		       	iconCls: 'delete',
				text: '<fmt:message key="button.delete"/>',
				handler: this.deletePostageTpl,
		    	scope: this
		    }
		    </jkd:haveAuthorize>
		    ]
    	});
        
        this.postageTplList = Ext.create('MyExt.productManager.PostageTemplateList', {
	       	region: 'center',
	        header: false,
	        autoScroll: true
        });
   
    	this.items = [this.menuTreePanel, this.postageTplList];	
    	this.callParent(arguments);
	    
	    this.menuTreePanel.on('itemclick', function(treePanel, record, item, index, e){
	    	e.stopEvent();
	    	if(record.data.leaf){
	    		<jkd:haveAuthorize access="/postageTpl/getRegionListByTemplateId.json">
	    		this.postageTplList.transferData(record);
	    		</jkd:haveAuthorize>
	    	}
	    }, this);
	    
	    this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	warehouseId: this.menuTreePanel.warehouseId
			});
	    }, this);
	    
    	this.menuTreePanel.on('itemcontextmenu', function(view, record, item, index, e){
    		e.stopEvent();
    		
    		this.menu.items.each(function(m){
				m.hide();
				if(!record.raw.leaf && m.iconCls == 'add'){
					m.show();
				}else if(record.raw.leaf && m.iconCls == 'delete'){
					m.show();
				}else if(record.raw.leaf && m.iconCls == 'Chartpieadd'){
					m.show();
				}
			});
			
			this.menu.record = record;
	    	this.menu.showAt(e.getXY());		
		}, this);
    },
    
    addPostageTpl : function(){
    	var isFreePostage = (this.menu.record.data.warehouseId == 0);
    	var postageTplFormPanel = Ext.create('MyExt.productManager.PostageTemplateFormPanel', {
    		tplType: 'add',
    		record: this.menu.record,
    		isFreePostage: isFreePostage
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	                	postageTplFormPanel.form.submit({
	                		waitMsg: 'Loading...',
	                   		url: '<c:url value="/postageTpl/createPostageTpl.json"/>',
	                   		scope: this,
	                   	 	success: function(form, action) {
	                    		var responseObject = Ext.JSON.decode(action.response.responseText);
	                       		if(responseObject.error == false){
	                       			showSuccMsg(responseObject.message);
	                       			this.menuTreePanel.warehouseId = responseObject.warehouseId;
	                        		this.store.reload();
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
      	openWin('<fmt:message key="postage.template.add"/>', postageTplFormPanel, buttons, 400, 300);
    },
    
    editPostageTpl : function(){ 			
		var textAreaMsg = Ext.create('Ext.form.TextField', {
 			fieldLabel: '<fmt:message key="postage.template.name"/>',
    		allowBlank: false,
    		labelWidth: 60,
    		value: this.menu.record.data.text,
       		anchor: '100%'
 		});	
 		
 		var freePostageAmountMsg = Ext.create('Ext.form.TextField', {
 			fieldLabel: '<fmt:message key="postage.template.pinkageAmount"/>',
    		allowBlank: false,
    		labelWidth: 60,
    		hidden: !(this.menu.record.data.freePostageAmount > 0),
    		value: this.menu.record.data.freePostageAmount,
       		anchor: '100%'
 		});	
 		
	 	
	 	var buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			scope: this,  
	        handler: function(){
				if(textAreaMsg.getValue() == null || textAreaMsg.getValue().length == 0){ 
					showWarnMsg('<fmt:message key="order.close.confirm"/>', 8);
					return;
				}
				
		     	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/postageTpl/editPostageTemplate.json"/>',
				         	method: 'post',
							scope: this,
							params:{templateId: this.menu.record.data.templateId, name: textAreaMsg.getValue(), freePostageAmount: freePostageAmountMsg.getValue()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.error == false){
		                       		showSuccMsg(responseObject.message);
		                        	this.menuTreePanel.warehouseId = responseObject.warehouseId;
	                        		this.store.reload();
	                        		popFormWin.close();
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
      	openFormWin('<fmt:message key="postage.template.edit"/>', [textAreaMsg, freePostageAmountMsg], buttons, 350, 160);
    },
    
    deletePostageTpl : function(){
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/postageTpl/deletePostageTemplate.json"/>',
		         	method: 'post',
					scope: this,
					params:{templateId: this.menu.record.data.templateId},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
                       		showSuccMsg(responseObject.message);
                       		this.menuTreePanel.warehouseId = responseObject.warehouseId;
                        	this.store.loadPage(1);
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	     	}
	 	}, this)
    },
    
	booleanRenderer: function(value, meta, record) {  
		if(value == 0) {
			return '<span style="color:green;"><fmt:message key="button.no"/></span>';
		}else if(value == 1){
			return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
		} 
		return value;
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});