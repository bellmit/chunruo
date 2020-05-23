Ext.define('InitModel', {
	extend: 'Ext.data.Model',
	fields: [
	   {name: 'id', type: 'int'},
	   {name: 'strId', type: 'string'},
	   {name: 'specModelId', type: 'int'},
	   {name: 'warehouseId', type: 'int'},
	   {name: 'name', type: 'string'},
	   {name: 'namePath', type: 'string'},
	   {name: 'filePath', type: 'string'}
	]
});

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

function showImageWin(src, name){
	var imageWindow = Ext.create('Ext.window.Window', {
		id: 'siteImageWin' + src,
		plain: true,
		modal: true,
		layout:'fit',
		title: name,
		items: [{
			xtype: 'panel',
			header: false,
			html: '<img src=' + src + ' onclick="javascript:hideImageWin(\'' +src + '\')"/>'
		}]
	});
	imageWindow.show(); 
}

function hideImageWin(src){
	Ext.getCmp('siteImageWin' + src).close();
}