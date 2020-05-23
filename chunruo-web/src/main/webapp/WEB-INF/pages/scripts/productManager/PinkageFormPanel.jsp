<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.PinkageFormPanel', {
    extend : 'Ext.form.Panel',
 	header: false,
 	labelAlign: 'right',
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
    
    
	initComponent : function(config) {
		Ext.apply(this, config);
		     this.items = [{
				xtype: 'hiddenfield', 
				name: 'templateId',
			    value: this.templateId,
			    id:'templateIds'
			},{
				xtype: 'hiddenfield', 
				name: 'isFreeTemplate',
				id:'isFreeTemplates',
			    value: this.isFreeTemplate
			},{
				xtype:'textfield',
				labelWidth: 60,
				fieldLabel: '<fmt:message key="postage.template.pinkageAmount"/>',
				name: 'freePostageAmount',
				anchor:'97%',
				allowBlank: false,
				value:this.freePostageAmount
			},{
			            xtype: 'checkboxgroup',
                        id: 'areas',
                        name: 'area',
                        fieldLabel: '<fmt:message key="postage.template.pinkageArea" />',
                        anchor: '98%',                  
						columns: 4,
						vertical: true,
						bodyPadding: 10,
						name: 'area',
                        listeners: {
                             render: function (view, opt) {
                   var isFreeTemplate=Ext.getCmp('isFreeTemplates').getValue();
                   var templateId=Ext.getCmp('templateIds').getValue();
                                Ext.Ajax.request({
        url: '<c:url value='/postageTemplate/provincesList.json'/>',
        params: {templateId:templateId,isFreeTemplate:isFreeTemplate},
        success: function (response) {
            var obj = Ext.JSON.decode(response.responseText);
            var len = obj.data.length;//obj.data.length; "Table"这里的Table指的是后台返回 类似于data
            
            if (obj.data == null || len == 0) {
                return;
            }
            
            if(obj.status == "1"){
            var checkedLen = obj.checkedList.length;
            }
            var checkboxgroup = Ext.getCmp("areas");
            var form =Ext.getCmp('forms');
            for (var i = 0; i < len; i++) {
                    
                    var checkbox = new Ext.form.Checkbox(
                  {
                      name: obj.data[i].areaId,
                      boxLabel: obj.data[i].shortName,//obj.Table[i].Title; "Title"指的是返回的名字
                      inputValue: obj.data[i].areaId,   
                      checked: false,
                      });
                      
                      if(obj.status == "1"){
                      if(obj.checkedList !=null && checkedLen !=0){
                        for ( var j = 0; j < checkedLen; j++) {
                  if(obj.data[i].areaId == obj.checkedList[j].areaId){
                var checkbox = new Ext.form.Checkbox(
                  {
                      name: obj.data[i].areaId,
                      boxLabel: obj.data[i].shortName,
                      inputValue: obj.data[i].areaId,   
                      checked: true,
                      })
                 }

                  }
                  }
                  };
                checkboxgroup.items.add(checkbox);
            };  
           
         //重新调整版面布局  
         Ext.getCmp('pinkageFormPanel').setWidth(500)
        },

    });
                            }
                        },
                      
			}];
		this.reader = new Ext.data.JsonReader({
			totalProperty: 'total',
			model: 'PostageTemplate',
        	root: 'data'
		});
		
		this.bbar = [ {  
                    text: '<fmt:message key="postage.template.invertSelect" />',  
                    handler: function() {  
                        var array = Ext.getCmp('areas').items;  
                        array.each(function(item){  
//                          alert(item.getValue());  
                            if(item.getValue()==true){  
                                item.setValue(false);  
                            }else{  
                                item.setValue(true);  
                            }  
                        });  
                    }  
                }, 
	    '-',
                {  
                    text: '<fmt:message key="postage.template.allSelect" />',  
                    handler: function() {  
                        var array = Ext.getCmp('areas').items;  
                        array.each(function(item){  
                            item.setValue(true);  
                        });  
                    } 
                    
	    }];
    	this.callParent();
    }
});