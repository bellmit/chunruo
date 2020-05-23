<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('DProductCategory', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'categoryId',	type: 'int'},
		{name: 'name',	 		type: 'string'},
		{name: 'pathName',	 	type: 'string'},
		{name: 'description',	type: 'string'},
		{name: 'parentId',	 	type: 'string'},
		{name: 'imagePath',	 	type: 'string'},
		{name: 'status',	 	type: 'string'},
		{name: 'sort',	 		type: 'string'},
		{name: 'level',	 		type: 'string'},
		{name: 'profit',	 	type: 'string'},
		{name: 'createTime',	type: 'string'},
		{name: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.DateSelectorPicker', {
    extend: 'Ext.form.field.Picker',
    alias: 'widget.dateSelectorPicker',
    triggerCls : Ext.baseCSSPrefix + 'form-date-trigger',
    hiddenData: null,
    matchFieldWidth: false,
    
    initComponent : function(config) {
		Ext.apply(this, config);
		this.on('expand', function(picker, options) {
			var pickerValue = this.getValue();
			if(this.getPicker().items.length > 0){
				this.getPicker().items.each(function(form) {
					this.getPicker().remove(form, false);
			        form.hide();
				}, this);
			}
			
			if(pickerValue == null || pickerValue == ''){
				this.addPickerItems();
			}else{
				var dateArray = pickerValue.split(","); 
				for (i = 0; i < dateArray.length; i++ ){
					var operatorArray = dateArray[i].split("|"); 
					if(operatorArray != null && operatorArray.length == 2){
						this.addPickerItems(operatorArray[0], operatorArray[1]);
					}
				} 
			}
		}, this);
		this.callParent();
	},
    
    createPicker: function() {
    	this.operatorStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{strId: '=', name: '<fmt:message key="button.operator.equalto"/>'},
        		{strId: '<>', name: '<fmt:message key="button.operator.not.equalto"/>'},
        		{strId: '>', name: '<fmt:message key="button.operator.greater.than"/>'},
        		{strId: '>=', name: '<fmt:message key="button.operator.greater.than.or.equalto"/>'},
        		{strId: '<', name: '<fmt:message key="button.operator.less.than"/>'},
        		{strId: '<=', name: '<fmt:message key="button.operator.less.than.or.equal.to"/>'}
        	]
        });
       	
    	var dateSelectorPicker = Ext.create('Ext.Panel', {
    		minHeight: 50,
            minWidth: 250,
            floating: true,
			region: 'center',
			header: false,
			collapsible: true,
            scroll: 'both',
	        header: false,
		 	buttonAlign: 'center',
		 	hideLabel: true,
		    defaultType: 'textfield',
		    autoScroll: true,
		    items:[],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
		    bbar:['->',{
	        	hideLabel: true,
	        	iconCls: 'add',
	        	handler : function(){
	        		this.addPickerItems();
	        	},
	        	scope: this
	        },{
	        	text: '<fmt:message key="button.confirm"/>', 
	        	scope: this,
	        	handler : function(){
	        		var pickerValue = '';
	        		var isSuccess = true;
	        		var isFirst = true;
	        		this.getPicker().items.each(function(form) {
	        			if(!form.isValid()){
	        				isSuccess = false;
	        			}
	        			
	        			if(!isFirst){
	        				pickerValue += ',';
	        			}
	        			
	        			var operatorKey = form.down('combobox').getValue();
	        			var strDate = form.down('datefield').getRawValue();
	        			pickerValue += operatorKey + '|' + strDate;
	        			isFirst = false;
					}, this);
					
					if(isSuccess){
						this.setValue(pickerValue);
	        			this.collapse();
					}
	        	}
	        }]
    	});  
        return dateSelectorPicker;
    },
    
    addPickerItems : function(operatorKey, strDate){
    	var form = Ext.create('Ext.form.Panel', {
       	 	layout: 'column',  
        	border: false,  
        	items: [{    
            	xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 2px 2px 2px;',
            	items: [{
	        		hideLabel: true,
	        		width: 110,
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'strId',
			        store: this.operatorStore,
			        style: 'padding: 0px 4px',
			        value: operatorKey,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true
				},{    
					hideLabel: true, 
					style: 'padding: 0px 4px',
					width: 120,
                	editable: false,  
                	xtype: 'datefield',
			        anchor: '100%',
			        value: strDate,
			        format: 'Y-m-d',
			        allowBlank: false,
			        maxValue: new Date()  
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'delete',
			        scope: this,
			        handler: function(){
			        	this.getPicker().remove(form, false);
			        	form.hide();  
			        } 
	        	}]
        	}]
    	});
    	this.getPicker().add(form);
    },
    
    booleanRenderer: function(value, meta, record) {    
       	if(value == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }  
   	}
});
