<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductSpecComboEditor', {
	extend: 'Ext.Editor',
    alias: 'plugin.dataviewcomboeditor',
 
    alignment: 'tl-tl',
 
    completeOnEnter: true,
 
    cancelOnEsc: true,
 
    shim: false,
 
    autoSize: {
        width: 'boundEl',
        height: 'field'
    },
 
    labelSelector: 'x-editable',
 
    requires: [
        'Ext.form.field.ComboBox'
    ],
 
    constructor: function(config) {
    	this.warehouseStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="warehouse" varStatus="status" items="${allProductWarehouseLists}" >
				{id: ${warehouse.value.warehouseId}, name: '${warehouse.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});
		
        config.field = config.field || Ext.create('Ext.form.field.ComboBox', {
        	store: this.warehouseStore,
            allowOnlyWhitespace: false,
            selectOnFocus:true,
            displayField: 'name',
	        valueField: 'id',
	        queryMode: 'local',
	        editable: false,
	        allowBlank: false,
	        typeAhead: true
        });
        this.callParent([config]);
    },
 
    init: function(view) {
        this.view = view;
        this.mon(view, 'afterrender', this.bindEvents, this);
        this.on('complete', this.onSave, this);
    },
 
    // initialize events 
    bindEvents: function() {
        this.mon(this.view.getEl(), {
            click: {
                fn: this.onClick,
                scope: this
            }
        });
    },
 
    // on mousedown show editor 
    onClick: function(e, target) {
        var me = this,
            item, record;
 
        if (Ext.fly(target).hasCls(me.labelSelector) && !me.editing && !e.ctrlKey && !e.shiftKey) {
            e.stopEvent();
            item = me.view.findItemByChild(target);
            record = me.view.store.getAt(me.view.indexOf(item));
            me.startEdit(target, record.data[me.dataIndex]);
            me.activeRecord = record;
        } else if (me.editing) {
            me.field.blur();
            e.preventDefault();
        }
    },
 
    // update record 
    onSave: function(ed, value) {
    	var records = this.warehouseStore.query('id', value);
    	if(records != null && records.length > 0){
    		this.activeRecord.set(this.dataIndex, records.getAt(0).get('name'));
    	}
        this.activeRecord.set(this.dataValueIndex, value);
    }
});

