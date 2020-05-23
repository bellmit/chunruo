<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.ApplyAgentStoreFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.applyAgentStoreForm'],
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
            xtype: 'hidden',
            name: 'storeId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="store.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.storeId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'storeId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.userId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.name"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'name',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.editNameCount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'editNameCount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.logo"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'logo',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.qcode"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'qcode',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.saleCategoryFid"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'saleCategoryFid',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.linkman"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'linkman',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.mobile"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'mobile',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.introduce"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'introduce',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.isApprove"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isApprove',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.status"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'status',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.publicDisplay"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'publicDisplay',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.serviceTel"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'serviceTel',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.serviceQq"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'serviceQq',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.serviceWeixin"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'serviceWeixin',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.openNav"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'openNav',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.navStyleId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'navStyleId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.useNavPages"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'useNavPages',
				    anchor:'97%'
				}, {
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.createTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				}]
				},{
			       xtype: 'container',
			       flex: 1,
			       layout: 'anchor',
			       items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.sales"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'sales',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.income"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'income',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.balance"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'balance',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.unbalance"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'unbalance',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.orders"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orders',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.storePayIncome"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'storePayIncome',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.withdrawalAmount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'withdrawalAmount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.withdrawalType"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'withdrawalType',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.bankId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'bankId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.bankCard"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'bankCard',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.bankCardUser"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'bankCardUser',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.openingBank"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'openingBank',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.collect"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'collect',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.isOfficialShop"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isOfficialShop',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.isWholesale"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isWholesale',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.inviterCode"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'inviterCode',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.editWholesale"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'editWholesale',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.storePageId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'storePageId',
				    anchor:'97%'
				}, {
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.updateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				 
				}]
			   }]
        }];
    	this.callParent();
    }
});