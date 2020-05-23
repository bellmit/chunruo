<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('ImageModel', {
	extend: 'Ext.data.Model',
    fields: [
    	{name : 'fileId'},
		{name : 'fileName'},
		{name : 'filePath'},
		{name : 'fileSize'}, 
		{name : 'fileState'},
		{name : 'fileType'},
		{name : 'input_file'}
    ],
    idProperty: 'fileId'
});

Ext.define('MyExt.productManager.BackGroundImagePanel',{
	extend : 'Ext.container.Container',
	alias: ['widget.backGroundImagePanel'],
	viewHeight: 220,
	autoScroll: true,
	
	initComponent: function(config){
		Ext.apply(this, config);
	
		this.store = Ext.create('Ext.data.Store', {
     		autoDestroy: true,
     		model: 'ImageModel',
     		data: []
     	});
		
		this.tpl = new Ext.XTemplate(
			'<tpl for=".">'
			 + '<div class="thumb-wrap" style="text-align: center" >'
          		+ '<tpl if="fileType==\'2\'||fileType==\'.mp4\'||fileType==\'.3gp\'||fileType ==\'.wmv\'||fileType==\'.flv\'">'
			 	    + '<div class="x-panel x-form-label-left x-column" style="width:400px;">'
				      + '<video width="400" height="400"  controls="controls" autoplay><source src="{filePath}" type="video/mp4"><source src="{filePath}" type="video/ogg"></video>'
				    + '</div>'
				+'</tpl>'
				+ '<tpl if="fileType==\'1\'||fileType==\'.jpg\'||fileType==\'.png\'||fileType==\'.gif\'||fileType==\'.jpeg\'">'
				    + '<div class="x-panel x-form-label-left x-column" style="width:120px;">'
				 	  + '<img src="{filePath}" title="{fileName}" width="120px" height="120px"/>'
				    + '</div>'
				+ '</tpl>'
			 + '</div>'
			 + '</tpl>'
		     + '<div class="x-clear"></div>',
		);
			
		this.dataView = Ext.create('Ext.view.View', {
			store: this.store,
			tpl: this.tpl,
			autoScroll: true,
			style: 'background: #f5f5f5 none repeat scroll 0 0;',
			itemSelector: 'div.thumb-wrap',
    		emptyText: 'No images available'
		});
		this.items = [this.dataView];
    	this.callParent(arguments);
	}
});