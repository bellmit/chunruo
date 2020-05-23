<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductSpecFieldSet', {
    extend: 'Ext.container.Container',
    mixins: {
        fieldAncestor: 'Ext.form.FieldAncestor'
    },
    alias: 'widget.productSpecFieldSet',
    uses: ['Ext.form.field.Checkbox', 'Ext.panel.Tool', 'Ext.layout.container.Anchor', 'Ext.layout.component.FieldSet'],
    requires : ['MyExt.productManager.ProductSpecModelPicker'],
    checkboxUI: 'default',
    collapsed: false,
    toggleOnTitleClick : true,
    baseCls: Ext.baseCSSPrefix + 'fieldset',
    layout: 'anchor',
    descriptionText: '{0} field set',
    expandText: 'Expand field set',
    componentLayout: 'fieldset',
    ariaRole: 'group',
    focusable: false,
    autoEl: 'fieldset',
    checkbox: null,
 	stateEvents : [ 'collapse', 'expand' ],
    maskOnDisable: false,
    afterCollapse: Ext.emptyFn,
    afterExpand: Ext.emptyFn,
    childEls: [ 'body' ],
    style: 'background: #f5f5f5 none repeat scroll 0 0 !important;',
    isEditor: false,
 
    renderTpl: [
        '{%this.renderLegend(out,values);%}',
        '<div id="{id}-body" data-ref="body" class="{baseCls}-body {baseCls}-body-{ui} {bodyTargetCls}" ',
                'role="presentation"<tpl if="bodyStyle"> style="{bodyStyle}"</tpl>>',
            '{%this.renderContainer(out,values);%}',
        '</div>'
    ],
    
    initComponent: function() {
        var me = this,baseCls = me.baseCls;
        if (me.ariaRole && !me.ariaLabel) {
            me.ariaLabel = Ext.String.formatEncode(me.descriptionText, me.title || '');
        }
        
        me.ariaRenderAttributes = me.ariaRenderAttributes || {};
        me.ariaRenderAttributes['aria-expanded'] = !me.collapsed;
        me.initFieldAncestor();
        me.callParent();
 
        me.layout.managePadding = me.layout.manageOverflow = false;
        if (me.collapsed) {
            me.addCls(baseCls + '-collapsed');
            me.collapse();
        }
        if (me.title || me.checkboxToggle || me.collapsible) {
            me.addTitleClasses();
            me.legend = me.createLegendCt();
        }
        me.initMonitor();
    },

    initRenderData: function() {
        var me = this, data = me.callParent();
        data.bodyTargetCls = me.bodyTargetCls;
        me.protoBody.writeTo(data);
        delete me.protoBody;
        return data;
    },
 
    doDestroy: function() {
        var me = this,  legend = me.legend;
        if (legend) {
            delete legend.ownerCt;
            legend.destroy();
            me.legend = null;
        }
        me.callParent();
    },
 
    getState: function () {
        var state = this.callParent();
        state = this.addPropertyToState(state, 'collapsed');
        return state;
    },
 
    collapsedHorizontal: function () {
        return true;
    },
 
    collapsedVertical: function () {
        return true;
    },
 
    createLegendCt: function () {
        var me = this, items = [],
            legendCfg = {
                baseCls: me.baseCls + '-header',
                layout: 'container',
                ui: me.ui,
                id: me.id + '-legend',
                autoEl: 'legend',
                ariaRole: null,
                items: items,
                ownerCt: me,
                shrinkWrap: true,
                ownerLayout: me.componentLayout
            },
            legend;
        
       	// Title Button
		items.push({
			xtype: 'productSpecModelPicker',
	        displayField: 'name',
	        valueField: 'id',
	        queryMode: 'local',
	        typeAhead: true,
	        width: 80,
	        scope: this,
	        readOnly: this.isEditor,
	        listeners: {
                scope: this,
                itemClick: this.storeDataChanged 
            }
		},{
			xtype : 'label',
			hidden: true,
			style: 'display:inline;top: 4px;',
            text: this.title
        },{
			xtype : 'button',
            text: this.deleteTitle,
            style: 'margin-left:5px;',
            iconCls: 'delete', 	
            hidden: this.isEditor,
			scope: this,
            listeners: {
                scope: this,
                click: this.clickButton 
            }
        });
		
        legend = new Ext.container.Container(legendCfg);
        return legend;
    },
    
    setProductSpecLabel: function(isSecondarySpec){
    	if(isSecondarySpec){
    		this.legend.down('label').setHidden(true);
    	}else{
    		this.legend.down('label').setHidden(false);
    		this.legend.down('label').setWidth(485);
    		this.legend.down('label').setStyle({'display':'inline'});
    	}
    },
    
    clickButton: function(e){
        this.fireEvent('clickButton', this, this.fieldName, e);
    },
    
    storeDataChanged: function(picker, record, item, index, e, eOpts){
    	this.fireEvent('storeDataChanged', picker, record, item, index, e, eOpts);
    },
 
    createTitleCmp: function() {
        var me  = this,
            cfg = {
                html: me.title,
                ui: me.ui,
                cls: me.baseCls + '-header-text',
                id: me.id + '-legendTitle',
                ariaRole: 'presentation'
            };
 
        if (me.collapsible && me.toggleOnTitleClick) {
            cfg.listeners = {
                click : {
                    element: 'el',
                    scope : me,
                    fn : me.toggle
                }
            };
            cfg.cls += ' ' + me.baseCls + '-header-text-collapsible';
        }
        
        me.titleCmp = new Ext.Component(cfg);
        return me.titleCmp;
    },
    
    doRenderLegend: function (out, renderData) {
        var me = renderData.$comp,
            legend = me.legend,
            tree;
            
        if (legend) {
            legend.ownerLayout.configureItem(legend);
            me.setLegendCollapseImmunity(legend);
            tree = legend.getRenderTree();
            Ext.DomHelper.generateMarkup(tree, out);
        }
    },
 
    getCollapsed: function () {
        return this.collapsed ? 'top' : false;
    },
 
    getCollapsedDockedItems: function () {
        var legend = this.legend;
 
        return legend ? [ legend ] : [];
    },

    setTitle: function(title) {
        var me = this,
            legend = me.legend;
            
        me.title = title;
        me.ariaLabel = Ext.String.formatEncode(me.descriptionText, title || '');
        
        if (me.rendered) {
            if (!legend) {
                me.legend = legend = me.createLegendCt();
                me.addTitleClasses();
                legend.ownerLayout.configureItem(legend);
                me.setLegendCollapseImmunity(legend);
                legend.render(me.el, 0);
            }
            me.titleCmp.update(title);
            me.ariaEl.dom.setAttribute('aria-label', me.ariaLabel);
        } else if (legend) {
            me.titleCmp.update(title);
        } else {
            me.addTitleClasses();
            me.legend = me.createLegendCt();
        }
        return me;
    },
    
    addTitleClasses: function(){
        var me = this,
            title = me.title,
            baseCls = me.baseCls;
            
        if (title) {
            me.addCls(baseCls + '-with-title');
        }
        
        if (title || me.checkboxToggle || me.collapsible) {
            me.addCls(baseCls + '-with-legend');
        }
    },

    expand : function(){
        return this.setExpanded(true);
    },
 
    collapse : function() {
        return this.setExpanded(false);
    },
 
    setCollapsed: function(collapsed) {
        this.setExpanded(!collapsed);
    },

    setExpanded: function(expanded) {
        var me = this,
            checkboxCmp = me.checkboxCmp,
            toggleCmp = me.toggleCmp,
            operation = expanded ? 'expand' : 'collapse';
 
        if (!me.rendered || me.fireEvent('before' + operation, me) !== false) {
            expanded = !!expanded;
 
            if (checkboxCmp) {
                checkboxCmp.setValue(expanded);
            }
            else if (toggleCmp && toggleCmp.ariaEl.dom) {
                toggleCmp.ariaEl.dom.setAttribute('aria-checked', expanded);
            }
 
            if (expanded) {
                me.removeCls(me.baseCls + '-collapsed');
            } else {
                me.addCls(me.baseCls + '-collapsed');
            }
            
            if (me.ariaEl.dom) {
                me.ariaEl.dom.setAttribute('aria-expanded', !!expanded);
            }
            
            me.collapsed = !expanded;
            if (expanded) {
                delete me.getInherited().collapsed;
            } else {
                me.getInherited().collapsed = true;
            }
            if (me.rendered) { 
                me.updateLayout({ isRoot: false });
                me.fireEvent(operation, me);
            }
        }
        return me;
    },
    
    getRefItems: function(deep) {
        var refItems = this.callParent(arguments),
            legend = this.legend;
 
        if (legend) {
            refItems.unshift(legend);
            if (deep) {
                refItems.unshift.apply(refItems, legend.getRefItems(true));
            }
        }
        return refItems;
    },
    
    toggle: function() {
        this.setExpanded(!!this.collapsed);
    },
 
    privates: {
        applyTargetCls: function(targetCls) {
            this.bodyTargetCls = targetCls;
        },
 
        finishRender: function () {
            var legend = this.legend;
 
            this.callParent();
 
            if (legend) {
                legend.finishRender();
            }
        },
 
        getProtoBody: function () {
            var me = this,
                body = me.protoBody;
 
            if (!body) {
                me.protoBody = body = new Ext.util.ProtoElement({
                    styleProp: 'bodyStyle',
                    styleIsText: true
                });
            }
 
            return body;
        },
 
        getDefaultContentTarget: function() {
            return this.body;
        },
 
        getTargetEl : function() {
            return this.body || this.frameBody || this.el;
        },
 
        initPadding: function(targetEl) {
            var me = this,
                body = me.getProtoBody(),
                padding = me.padding,
                bodyPadding;
 
            if (padding !== undefined) {
                if (Ext.isIE8) { 
                    padding = me.parseBox(padding);
                    bodyPadding = Ext.Element.parseBox(0);
                    bodyPadding.top = padding.top;
                    padding.top = 0;
                    body.setStyle('padding', me.unitizeBox(bodyPadding));
                }
 
                targetEl.setStyle('padding', me.unitizeBox(padding));
            }
        },

        onCheckChange: function(cmp, checked) {
            this.setExpanded(checked);
        },
 
        setLegendCollapseImmunity: function(legend) {
            legend.collapseImmune = true;
            legend.getInherited().collapseImmune = true;
        },
 
        setupRenderTpl: function (renderTpl) {
            this.callParent(arguments);
 
            renderTpl.renderLegend = this.doRenderLegend;
        }
    }
});

