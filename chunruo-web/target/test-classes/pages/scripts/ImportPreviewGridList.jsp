<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.ImportPreviewGridList', {
    extend : 'Ext.grid.GridPanel',
	region: 'center',
	header: false,
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	isCustomQuery: false,
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
    	this.callParent();
    }
});