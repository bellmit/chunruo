<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('MyExt.storeManager.DistributionPageFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.pageForm'],
    requires : [],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		this.items = [{
			xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="fx.page.creat"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				  		labelWidth: 85,
			           	fieldLabel: '<fmt:message key="fx.channel.channelName" />',
			           	name : 'channelId',  
			            xtype:'combobox',
			            emptyText : '<fmt:message key="button.select"/>', 
			            triggerAction : 'all',  
			            store : Ext.create(Ext.data.Store,{  
			                    	autoLoad : false,  
			                        model:'InitModel',
			                        proxy : {  type:'ajax',
			                                    url: '<c:url value="/channel/list.json"/>',
			                        reader : {	type:'json',
			                        			root:'data',
			                       				totalProperty:'totalCount'
			                       				}	
			                       			},
			                    }),
			                 
			           displayField : 'channelName',  
			           valueField : 'channelId',  
			           loadingText : '<fmt:message key="ajax.loading"/>',  
			           queryMode : 'remote',  
			           forceSelection : true,  
			           allowBlank : false,  
			           typeAhead : true,  
			           resizable : true,  
			           editable : true,  
			           anchor : '97%',
			           listeners : {  
			            'beforequery':function(e){  
			                var combo = e.combo;    
			                if(!e.forceAll){
			                    var input = e.query;
			                    var regExp = new RegExp(".*" + input + ".*");  
			                    combo.store.filterBy(function(record,id){
			                        var text = record.get(combo.displayField);    
			                        return regExp.test(text);   
			                    });  
			                    combo.expand();    
			                    return false;  
			                }  
			            }  
			        }  
			    },{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.page.pageName"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'pageName',
				    anchor:'97%'
				},{
				  	labelWidth: 85,
		       		width: 200,
		            name: 'typeId',
		            fieldLabel:'<fmt:message key="fx.page.categoryType"/>',
					xtype:'combo',
					valueField:'key',
					displayField:'value',
					emptyText : '<fmt:message key="button.select"/>', 
					hiddenName:'categoryType',
					store: new Ext.data.ArrayStore({  
		                        fields : ['key', 'value'],  
		                        data : [["0", '<fmt:message key="fx.page.categoryType.0" />'],
		                               ["1", '<fmt:message key="fx.page.categoryType.1" />']]  
		                    }), 
					mode : 'local',
					triggerAction : 'all'
				}]
			}]
        }];
     	this.callParent();
    	
    	
    }
});