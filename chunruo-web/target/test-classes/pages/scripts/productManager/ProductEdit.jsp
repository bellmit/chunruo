<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductEdit', {
	extend:'Ext.form.Panel',
	closable: true,
	layout: 'border',
	header: false,
	viewConfig: {
		stripeRows : true,
		enableTextSelection : true
	},
    
	initComponent : function(config) {
		Ext.apply(this, config);
	
		this.ueditorId = Ext.String.format('ueditor-{0}', this.id);
		this.items = [{
			xtype: 'component',
			name: 'htmlContent',
			width: 350,
			header: false,
			region: 'center',
			html: this.recordData.productDesc,
	    	cls: 'x-rich-media',
   	 		scope : this,
    		style: 'overflow:scroll;color:#FFFFFF;backgroundColor:#FFFFFF;'
		},{
			xtype: 'component',
			name: 'htmlEditor',
            width: 450,
            region: 'east',
            border: true,
            header: false,
            scope : this,  
            html: Ext.String.format('<script id="{0}" type="text/plain"></script>', this.ueditorId),
            listeners: {
            	scope : this,  
                boxready: function (t, layout, opts) {
                	try{
	                	var height = t.getHeight() - 80;
	                    this.ueditor = UE.getEditor(this.ueditorId, {
	                    	serverUrl: '<c:url value="/textarea/textEditor.msp"/>',
	                    	initialFrameWidth: t.getWidth(),
	       					initialFrameHeight: height
	                    });
	                    
	                    this.ueditor.addListener('ready', (function(thiz){
			       			return function(){
			       				thiz.ueditor.setHeight(height);
			       				thiz.ueditor.setContent(thiz.recordData.productDesc);
							}
						})(this));
						
						this.ueditor.addListener('selectionchange', (function(thiz){
			       			return function(){
			       				thiz.down('[name=htmlContent]').update(thiz.ueditor.getContent());
							}
						})(this));
					}catch(e){}
				},
				destroy: function(){
					try{
						if(!this.ueditor){
							this.ueditor.destroy();
							this.ueditor = null;
						}
					}catch(e){}
				}
            }
        }];
		this.callParent(arguments);
    }
});