<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.MainPanel', {
    extend : 'Ext.TabPanel',
    requires : ['Ext.ux.TabCloseMenu'],
    activeTab : 0,
    enableTabScroll : true,		
	layoutOnTabChange : true,
	plugins: [Ext.create('Ext.ux.TabCloseMenu')],
	tabWidth : 120,

    initComponent : function(config) {
    	this.mask = new Ext.LoadMask(this, {msg:"Please wait..."});
    	Ext.apply(this, config);
    	this.items = [{
            id: 'main-view',
			layout: 'border',
	        border: false,
	        split: true,
            title: '<fmt:message key="webapp.control.panel" />',
            hideMode: 'offsets',
            items:[]
		}];
    	this.callParent(arguments);
    },
	
	createSouth: function(){
        this.south =  Ext.create('MyExt.contManager.PublishGrid', {
        	viewer: this.viewer,
        	header: false,
            layout: 'fit',
            border: false,
            split: true,
            region: 'south',
            autoScroll: true,
            enableColumnMove: true, 
            minHeight: 200,
            maxHeight: 350
        });
        return this.south;
    },
		
    openConfigTab : function(config) {
		if(!config && !config.id) return;
	    var tab = Ext.getCmp(config.id);
	    if(!tab){
	    	tab =  Ext.create(config.xtype, Ext.apply(config, {autoDestory: true})); 
	        this.add(tab).show();
	    }
	    this.setActiveTab(tab);
	},
	
	openTab : function(newTab) {
		var tab = this.queryById(newTab.id);
		if(!tab){
			tab = newTab;
			this.add(tab).show();
		}
		this.setActiveTab(tab);
	}
});
