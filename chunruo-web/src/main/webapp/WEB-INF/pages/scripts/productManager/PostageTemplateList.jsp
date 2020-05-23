<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('PostageVo', {
	extend: 'Ext.data.Model',
	idProperty: 'areaId',
    fields: [
    	{name: 'areaId',		mapping: 'areaId',			type: 'int'},
		{name: 'firstWeigth',	mapping: 'firstWeigth',		type: 'string'},
		{name: 'firstPrice',	mapping: 'firstPrice',		type: 'string'},
		{name: 'afterWeigth',	mapping: 'afterWeigth',		type: 'string'},
		{name: 'afterPrice',	mapping: 'afterPrice',		type: 'string'},
		{name: 'productType',	mapping: 'productType',		type: 'int'},
		{name: 'templateId',	mapping: 'templateId',		type: 'string'},
		{name: 'templateName',	mapping: 'templateName',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.PostageTemplateList', {
    extend : 'Ext.grid.GridPanel',
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
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
			model: 'PostageVo',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/postageTpl/getRegionListByTemplateId.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
			{text: '<fmt:message key="postage.template.areaId"/>', dataIndex: 'areaId', width: 50, sortable : true},
			{text: '<fmt:message key="postage.template.templateName"/>', dataIndex: 'templateName', width: 100, sortable : true},
			{text: '<fmt:message key="postage.template.area"/>', dataIndex: 'area', width: 500, sortable : true},
        	{text: '<fmt:message key="postage.template.firstWeigth"/>', dataIndex: 'firstWeigth', width: 80, sortable : true},
        	{text: '<fmt:message key="postage.template.firstPrice"/>', dataIndex: 'firstPrice', width: 80, sortable : true},
        	{text: '<fmt:message key="postage.template.afterWeigth"/>', dataIndex: 'afterWeigth', width: 80, sortable : true},
        	{text: '<fmt:message key="postage.template.afterPrice"/>', dataIndex: 'afterPrice', width: 80, sortable : true},
            {text: '<fmt:message key="postage.template.packageWeigth"/>', dataIndex: 'packageWeigth', width: 80, sortable : true},
        ];
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/postageTpl/getRegionListByTemplateId.json">
	    {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/postageTpl/getRegionTplArea.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="postage.template.addRegion"/>', 
        	iconCls: 'add', 	
        	handler: this.addPostageRegion,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/postageTpl/deletePostageRegion.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="postage.template.deleteRegion"/>', 
        	iconCls: 'delete', 	
        	handler: this.deletePostageRegion,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
    	this.callParent();
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	templateId: this.record.data.templateId,
	        	templateName: this.record.data.name
			});
	    }, this);
	    
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/postageTpl/getRegionTplArea.json">
    	this.on('itemdblclick', this.onDbCslick, this);
    	</jkd:haveAuthorize>
    },
    
    transferData : function(record){
    	this.record = record;
    	this.store.load();
    },
    
    onDbCslick : function(view, record, item, index, e, eOpts){
        var areaId = record.data.areaId;
        var isFreeTemplate = this.record.data.isFreeTemplate;
        var templateId = this.record.data.templateId;
        var productType = record.data.productType;
        console.log(productType);
        var isHideRegion = false;
        if(areaId == 1 && isFreeTemplate != 'true'){
        isHideRegion = true;
        }
		var postageRegionFormPanel = Ext.create('MyExt.productManager.PostageRegionFormPanel', {id: 'edit@PostageRegionFormPanel',isHideRegion : isHideRegion,productType:productType});
		postageRegionFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/postageTpl/getRegionTplArea.json"/>', 
    		params: {templateId: this.record.data.templateId, areaId: areaId}, 
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
		
		var buttons = [{
		    id: 'save',
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(postageRegionFormPanel.isValid()){           
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                postageRegionFormPanel.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/postageTpl/saveRegionTplArea.json"/>',
			                    scope: this,
			                    params: {templateId: this.record.data.templateId, areaId: record.data.areaId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.load();
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form,action){
			                       showFailMsg(responseObject.message,4);
			                    }
			        		})
			        	}
			        	rowsData = [];
			        }, this)
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="postage.template.editRegion"/>', postageRegionFormPanel, buttons, 600,500);
    },
    
    deletePostageRegion : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.areaId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="postage.template.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/postageTpl/deletePostageRegion.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), templateId : this.record.data.templateId},
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

    addPostageRegion : function(){
    	var postageRegionFormPanel = Ext.create('MyExt.productManager.PostageRegionFormPanel', {id: 'add@PostageRegionFormPanel'});
		postageRegionFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/postageTpl/getRegionTplArea.json"/>', 
    		params: {templateId: this.record.data.templateId}, 
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
		
		var buttons = [
		<jkd:haveAuthorize access="/userSys/saveAdminUser.json">
		{
            id: 'save',
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(postageRegionFormPanel.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){		
			                postageRegionFormPanel.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/postageTpl/saveRegionTplArea.json"/>',
			                    scope: this,
			                    params:{templateId: this.record.data.templateId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form,action){
			                       showFailMsg(responseObject.message,4);
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
		openWin('<fmt:message key="postage.template.addRegion"/>', postageRegionFormPanel, buttons, 600,500);
	}
});