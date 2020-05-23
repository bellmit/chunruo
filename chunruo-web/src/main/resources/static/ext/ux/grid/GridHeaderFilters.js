Ext.define('FilterField.button.OperatorButton', {
    extend: 'Ext.AbstractPlugin',
    alias: 'plugin.operatorbutton',
    mixins: {
        observable: 'Ext.util.Observable'
    },

    autoHide: true,

    operatorButtonCls: Ext.baseCSSPrefix + 'operator-button',

    operator: 'eq',
    operators: ['eq', 'gte', 'lte', 'ne', 'gt', 'lt'],

    textField: undefined,
    operatorSet: {
        eq: {
            text: 'Is equal to',
            iconCls: Ext.baseCSSPrefix + 'operator-button-equal',
            value: '='
        },
        ne: {
            text: 'Is not equal to',
            iconCls: Ext.baseCSSPrefix + 'operator-button-not-equal',
            value: '!='
        },
        gte: {
            text: 'Great than or equal',
            iconCls: Ext.baseCSSPrefix + 'operator-button-great-than-equal',
            value: '>='
        },
        lte: {
            text: 'Less than or equal',
            iconCls: Ext.baseCSSPrefix + 'operator-button-less-than-equal',
            value: '<='
        },
        gt: {
            text: 'Great than',
            iconCls: Ext.baseCSSPrefix + 'operator-button-great-than',
            value: '>'
        },
        lt: {
            text: 'Less than',
            iconCls: Ext.baseCSSPrefix + 'operator-button-less-than',
            value: '<'
        }
    },

    constructor: function(config) {
        var me = this;

        Ext.apply(me, config);
        me.mixins.observable.constructor.call(me);
        me.callParent(arguments);
    },

    init: function(textField) {

        var me = this,
            items = [];

        me.operator = (Ext.Array.contains(me.operators, me.operator) ? me.operator : me.operators[0]);
        me.task = Ext.create('Ext.util.DelayedTask');

        Ext.each(me.operators, function(op) {
            items.push(Ext.apply(Ext.clone(me.operatorSet[op]), {
                handler: me.onOperatorClick,
                scope: me
            }));
        });
        me.textField = textField;
        me.menu = Ext.create('Ext.menu.Menu', {
            items: items,
            listeners: {
                hide: me.onMenuHide,
                scope: me
            }
        });

        if (!textField.rendered) {
            textField.on('afterrender', me.onFieldRender, me, { single: true });
        } else {
            me.onFieldRender();
        }

        textField.on({
            destroy: me.onFieldDestroy,
            focus: me.onFieldFocus,
            blur: me.onFieldBlur,
            resize: me.onFieldResize,
            scope: me
        });
    },

    onFieldRender: function(textField) {
        var me = this,
            bodyEl = me.textField.bodyEl,
            btn;

        btn = me.operatorButtonEl = me.textField.bodyEl.createChild({
            tag: 'div',
            cls: me.operatorButtonCls + ' ' + me.operatorSet[me.operator].iconCls,
            style: 'visibility: hidden;',
            'data-qtip': me.operatorSet[me.operator].text
        });

        bodyEl.on('mouseover', me.onFieldMouseOver, me);
        bodyEl.on('mouseout', me.onFieldMouseOut, me);
        btn.on('mouseover', me.onButtonMouseOver, me);
        btn.on('mouseout', me.onButtonMouseOut, me);
        btn.on('click', me.onButtonClick, me);

        me.repositionOperatorButton();
        me.updateOperatorButtonVisibility();
    },

    onFieldDestroy: function() {
        var me = this;

        me.operatorButtonEl.destroy();
        me.menu.destroy();
    },

    onFieldFocus: function() {
        var me = this;

        me.fieldInFocus = true;
        me.updateOperatorButtonVisibility();
    },

    onFieldBlur: function() {
        var me = this;

        me.fieldInFocus = false;
        me.updateOperatorButtonVisibility();
    },

    onFieldResize: function() {
        var me = this;

        me.repositionOperatorButton();
    },

    onFieldMouseOver: function(e) {
        var me = this;

        if (me.textField.triggerEl) {
            if (e.getRelatedTarget() == me.textField.triggerEl.elements[0].dom) {
                return;
            }
        }
        me.operatorButtonEl.addCls(me.operatorButtonCls + '-mouse-over-input');
        if (e.getRelatedTarget() == me.operatorButtonEl.dom) {
            // Moused moved to operator button and will generate another mouse event there.
            // Handle it here to avoid duplicate updates (else animation will break)
            me.operatorButtonEl.removeCls(me.operatorButtonCls + '-mouse-over-button');
            me.operatorButtonEl.removeCls(me.operatorButtonCls + '-mouse-down');
        }
        me.updateOperatorButtonVisibility();
    },

    onFieldMouseOut: function(e) {
        var me = this;

        if (me.textField.triggerEl) {
            if (e.getRelatedTarget() == me.textField.triggerEl.elements[0].dom) {
                return;
            }
        }
        me.operatorButtonEl.removeCls(me.operatorButtonCls + '-mouse-over-input');
        if (e.getRelatedTarget() == me.operatorButtonEl.dom) {
            // Moused moved from operator button and will generate another mouse event there.
            // Handle it here to avoid duplicate updates (else animation will break)
            me.operatorButtonEl.addCls(me.operatorButtonCls + '-mouse-over-button');
        }
        me.updateOperatorButtonVisibility();
    },

    onButtonMouseOver: function(e) {
        var me = this;

        e.stopEvent();
        if (me.textField.bodyEl.contains(e.getRelatedTarget())) {
            // has been handled in handleMouseOutOfInputField() to prevent double update
            return;
        }
        me.operatorButtonEl.addCls(me.operatorButtonCls + '-mouse-over-button');
        me.updateOperatorButtonVisibility();
    },

    onButtonMouseOut: function(e) {
        var me = this;

        e.stopEvent();
        if (me.textField.bodyEl.contains(e.getRelatedTarget())) {
            // will be handled in handleMouseOverInputField() to prevent double update
            return;
        }
        me.operatorButtonEl.removeCls(me.operatorButtonCls + '-mouse-over-button');
        me.operatorButtonEl.removeCls(me.operatorButtonCls + '-mouse-down');
        me.updateOperatorButtonVisibility();
    },

    onButtonClick: function(e) {
        var me = this;

        if (e.button !== 0) return;
        me.menu.showAt(e.getX(), e.getY(), false);
        e.stopEvent();
    },

    onOperatorClick: function(item) {
        var me = this,
            btn = me.operatorButtonEl,
            field = me.textField,
            lastOperator = me.operator;

        Ext.each(me.operators, function(op) {
            btn.removeCls(me.operatorSet[op].iconCls);
        });
        btn.addCls(item.iconCls);
        btn.set({
            'data-qtip': item.text
        });

        me.operator = item.value;
        field.operator = item.value;
        field.focus();
        if (lastOperator != me.operator) {
            me.textField.fireEvent('operatorchanged', me.textField, me.operator, lastOperator);
            me.fireEvent('operatorchanged', me.textField, me.operator, lastOperator);
        }
    },

    onMenuHide: function() {
        var me = this;

        me.updateOperatorButtonVisibility();
    },

    shouldButtonBeVisible: function() {
        var me = this;

        if (me.autoHide
            && !me.menu.isVisible()
            && !me.fieldInFocus
            && !me.operatorButtonEl.hasCls(me.operatorButtonCls + '-mouse-over-button')
            && !me.operatorButtonEl.hasCls(me.operatorButtonCls + '-mouse-over-input')) {
            return false;
        }
        return true;
    },

    updateOperatorButtonVisibility: function() {
        var me = this,
            btn = me.operatorButtonEl,
            oldVisible = btn.isVisible(),
            newVisible = me.shouldButtonBeVisible(),
            padding;

        if (!Ext.isWebKit) {
            padding = (newVisible ? 18 : 0);
        } else {
            padding = (newVisible || !me.webKitBugFlag ? 18 : 0);
            me.webKitBugFlag = true;
        }
        me.textField.inputEl.applyStyles({
            'padding-left': padding + 'px'
        });


        me.task.delay(200, function() {
            var oldVisible = btn.isVisible(),
                newVisible = me.shouldButtonBeVisible();

            if (oldVisible == newVisible) return;

            btn.stopAnimation();
            btn.setVisible(newVisible, {
                duration: 0
            });

            if (!Ext.isWebKit) {
                padding = (newVisible ? 18 : 0);
            } else {
                padding = (newVisible || !me.webKitBugFlag ? 18 : 0);
                me.webKitBugFlag = true;
            }
            me.textField.inputEl.applyStyles({
                'padding-left': padding + 'px'
            });
        });
    },

    repositionOperatorButton: function() {
        var me = this,
            btn = me.operatorButtonEl;

        if (!btn) return;
        btn.alignTo(this.textField.bodyEl, 'tl-tl', [2, 4]);
    }
});
        

Ext.define('Ext.ux.grid.GridHeaderFilters', {
    extend: 'Ext.AbstractPlugin',
    alias: "plugin.gridHeaderFilters",

    require: [
        'FilterField.button.OperatorButton'
    ],

    /**
     * @param {string}  The key to the configs added to a grid column
     */
    activateKey: 'filter',


    /**
     * @param {Ext.util.DelayedTask}
     * @protected
     */
    _task: Ext.create('Ext.util.DelayedTask'),


    /**
     * @param {number}  The number of milliseconds to delay
     */
    delay: 2000,


    /**
     * Constructor
     * All args passed in are from configs when adding the plugin to the grid
     *
     * @param   {Object}    configs
     */
    constructor: function(configs) {

        this.activateKey = configs.activateKey || this.activateKey;
        this.delay = configs.delay || this.delay;
        this.filterMap = new Ext.util.HashMap();
        this.callParent(arguments);
    },


    /**
     * Init function
     * This function is called when the plugin is invoked by the Owner component
     *
     * @param   {Ext.grid.Panel}    grid
     */
    init: function(grid) {

        var me = this,columns = grid.columns;
        this.grid = grid;

        Ext.Array.each(columns, function(column) {

            var filter = {};

            if ( ! column[me.activateKey])
                return true; // Returning false breaks. Wat?

            if ( ! column.items)
                Ext.apply(column, { items: [] });

            column.filter = column[me.activateKey];

            Ext.apply(filter, column.filter, {
                width: '100%',
                triggers: {
                    clear: {
                        cls: 'x-form-clear-trigger',
                        hidden: true,
                        handler: function () {
                            this.setValue(null);

                            if (typeof this.clearValue === 'function')
                                this.clearValue();
                        }
                    }
                },
                listeners: {
                    change: {
                        fn: function (field) {
                            var fn = (Ext.isEmpty(field.getValue())) ? me.clearFilter : me.applyFilter;
                            me._task.delay(me.delay, fn, me, [field]);
                        }
                    }
                }
            });

            if (me.needsOperatorButton(column)) {
                Ext.apply(filter, me.getOperatorButtonPlugin());
            }

            column.insert(0, Ext.create('Ext.container.Container', {
                width: '100%',
                items: [filter],
                padding: '0 0px',
                listeners: {
                    scope: me,
                    element: 'el',
                    mousedown: function(e) { e.stopPropagation(); },
                    click: function(e) { e.stopPropagation(); },
                    dblclick: function(e) { e.stopPropagation(); },
                    keydown: function(e) { e.stopPropagation(); },
                    keypress: function(e) { e.stopPropagation(); },
                    keyup: function(e) { e.stopPropagation(); }
                }
            }));

        });

    },


    /**
     * Check if a column needs an Operator Button
     *
     * @param   {Ext.grid.Column} column
     * @returns {boolean}
     */
    needsOperatorButton: function(column) {
        return (column.filter.xtype == 'numberfield' || column.filter.xtype == 'datefield');
    },


    /**
     * Get the default filter operator for a column
     *
     * @param   {Ext.grid.Column}   column
     * @returns {string}
     */
    getDefaultOperator: function(column) {
        if (column.filter.xtype == 'textfield')
            return 'like';

        if (column.filter.xtype == 'combobox')
            return 'in';

        return 'eq';
    },


    /**
     * Get the configs for the OperatorButton plugin
     *
     * @returns {Object}
     */
    getOperatorButtonPlugin: function() {
        return {
            plugins: {
                ptype: 'operatorbutton',
                texteq: 'Equal to',
                textne: 'Does not equal',
                textgte: 'Greater than or equal',
                textlte: 'Less Than or equal to',
                textgt: 'Greater than',
                textlt: 'Less than',
                listeners: {
                    operatorchanged: function(field) {
                        field.fireEvent('change', field);
                    }
                }
            }
        }
    },


    /**
     * Clear a filter from the grid's underlying store
     *
     * @param   {Ext.form.field.Field}  field
     */
    clearFilter: function(field) {

        var column = field.ownerCt.ownerCt,
            grid = column.up('grid');

        var property = column.filter.property || column.dataIndex;
        this.filterMap.removeAtKey(property);
        field.triggers.clear.el.hide();
        column.setText(column.textEl.dom.firstElementChild.innerText);
        
        var filters = [];
        this.filterMap.each(function(key, value, length){
        	filters.push({key: key, value: value});
        });
        grid.getStore().loadPage(1);
        this.grid.filters = filters;
    },


    /**
     * Apply a filter on the grid's underlying store
     *
     * @param   {Ext.form.field.Field}  field
     */
    applyFilter: function (field) {

        var me = this,
            column = field.ownerCt.ownerCt,
            grid = column.up('grid');

        field.triggers.clear.el.show();
        column.setText('<strong><em>' + column.text + '</em></strong>');
        
        var property = column.filter.property || column.dataIndex;
        this.filterMap.add(property, field.getValue());
        
        var filters = [];
        this.filterMap.each(function(key, value, length){
        	filters.push({key: key, value: value});
        });
        grid.getStore().loadPage(1);
        this.grid.filters = filters;
    }
});