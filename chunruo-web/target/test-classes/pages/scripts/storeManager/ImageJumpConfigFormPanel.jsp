<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Jump', {
	extend: 'Ext.data.Model',
	idProperty: 'index',
	fields: [
		{name: 'index',	 			mapping: 'index',			type: 'int'},
    	{name: 'childrenId',	 	mapping: 'childrenId',		type: 'string'},
		{name: 'type',	 	        mapping: 'type',			type: 'string'},
		{name: 'pageOrProId',	 	mapping: 'pageOrProId',		type: 'string'}
		
    ]
});

Ext.define('MyExt.storeManager.ImageJumpConfigFormPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
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
        
        this.store = Ext.create('Ext.data.Store', {
        	
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Jump',
			id:'jumpStore',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/getJumpSettingByChildId.json?childrenId="/>' + this.childrenId,
				reader: {
					type : 'json',
                	root: 'data'
            	}
			}
		});
		
		 this.combostore = new Ext.data.ArrayStore({
	         fields: ['id', 'name'],
	         data: [[0, '<fmt:message key="image.jump.type.0"/>'],
	         		[2, '<fmt:message key="image.jump.type.2"/>']]
	    });
	  
	    
		
		this.combo = Ext.create('Ext.form.ComboBox', {  
           id: 'id_combo_OK',  
           name: 'slipInfo',  
           value: 0, // 设置默认选中值  
           store: this.combostore,  
           editable: false, // 设置为只可选择，不可编辑  
           queryMode: 'local', // 本地数据时使用'local'  
           displayField: 'name',  
           valueField: 'id'  
       });  
		 
       
		
			this.columns = [
			{text: '<fmt:message key="image.jump.index"/>', dataIndex: 'index', width: 60},
        	{text: '<fmt:message key="image.jump.childrenId"/>', dataIndex: 'childrenId', width: 80},
        	{text: '<fmt:message key="image.jump.type"/>', dataIndex: 'type', width: 120,  renderer : this.formatJumpType,  editor : this.combo},
        	{text: '<fmt:message key="image.jump.pageOrProId"/>', dataIndex: 'pageOrProId', width: 100,editor:{allowBlank:false}}
        ];
        
      
		
    	this.jumpList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'jumpList@jumpListPanel' + this.id,
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
			plugins: [Ext.create('Ext.grid.plugin.CellEditing',{clicksToEdit:1 }),
					'gridHeaderFilters',
					'gridexporter'
			],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	  

	    
    	this.items = [this.jumpList];	
		
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.jumpList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.jumpList.getSelectionModel();
	    
    },
  
    
    formatJumpType : function(val){ 
     	var str =  '';
		if(val == '0'){
			str = '<fmt:message key="image.jump.type.0"/>';
		}
		 if(val == '2'){
			str = '<fmt:message key="image.jump.type.2"/>';
		}
		return str;
	},
	 
           
	    
             
             
             //Combobox获取值
//             combobox.on('select', function () {
//                 alert(combobox.getValue());
//             })
  
});