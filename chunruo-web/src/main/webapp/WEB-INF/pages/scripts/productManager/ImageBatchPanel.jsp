<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ImageBatchPanel', {
	extend : 'Ext.Panel',
	region: 'center',
	closable: false,
	autoScroll: false,
	layout: 'border',
	split: true,
	viewConfig: {
		stripeRows : true,
		enableTextSelection : true
	},
    
	initComponent : function(config) {
		Ext.apply(this, config);

		this.store = Ext.create('Ext.data.Store', {
     		autoDestroy: true,
     		model: 'ImageModel',
     		data: []
     	});

        this.columns = [
			{header: '<fmt:message key="image.fileName"/>', width: 180, dataIndex: 'fileName', sortable: false, fixed: true},
			{header: '<fmt:message key="image.fileType"/>', width: 60, dataIndex: 'fileType', sortable: false, fixed: true, align: 'center'},
			{header: '<fmt:message key="image.progress"/>', width: 80, dataIndex: '', sortable: false, fixed: true, align: 'center', renderer: this.progressFormat, scope: this}, 
			{header: '&nbsp;', width: 45, dataIndex: 'fileState', renderer: this.formatState, sortable: false, fixed: true, align: 'center', scope: this}
		];

		this.scriptss = '<div class="details" style=" margin-left:30px;">' 
		+ '<table>' 
		+ '<tr align=middle>' 
		+ '<td align=middle>please choose one Image</td>' 
		+ '</tr>' 
		+ '</table>' 
		+'</div>';
		
		this.multipleFile = Ext.create('Ext.ux.form.MultipleFile', {
			iconCls: 'add',
			buttonText: '<fmt:message key="image.addFile"/>',
	        buttonOnly: true,
	        hideLabel: true,
	       	width: 80,
	        listeners: {
	        	scope: this,
	            'change': function(fb, v){
	            	var files = fb.fileInputEl.dom.files;
	            	if(files == null || files.length == 0)return;
					for(var i = 0; i < files.length; i ++ ){
						var file = files[i];
						var fileType = file.name.substring(file.name.lastIndexOf('.'), file.name.length);
						if(true){
							this.store.insert(this.store.getCount(), {
				                fileId: 'SWFUpload_' + Math.floor(Math.random()*10000) + file.name,
								fileName: file.name.substring(0, file.name.lastIndexOf('.')),
								fileType: fileType,
								fileState: this.fileList.FILE_STATUS.QUEUED,
								input_file: file
				            });
						}else{
							var message = '<fmt:message key="tools.parenthesis.left"/>' + file.name + '<fmt:message key="tools.parenthesis.right"/>';
							showWarnMsg(message + '<fmt:message key="resource.unsuppert.type"/>', 8);
						}
	            	}
	            }
	        }
	    });

		this.fileList = Ext.create('Ext.grid.GridPanel', {
			header: false,
			width: 400,
			region: 'west',
			store: this.store,
			columns: this.columns,
			columnLines: true,
			animCollapse: false,
    		enableLocking: true,
			FILE_STATUS: {QUEUED: -1, ERROR: -2, COMPLETE: -3, SUCCESS: 200},
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			columnLines: true,
			enableLocking: true,
			multiSelect: true,
			viewConfig: {
				stripeRows: true,
				enableTextSelection: true
			},
			tbar:[this.multipleFile,{
				id: 'db-icn-upload_file' + this.id,
				text : '<fmt:message key="image.startUpload"/>',
				iconCls : 'upload',
				handler : this.startUpload,
				scope : this
			},{
				id: 'db-icn-cross_file' + this.id,
				text : '<fmt:message key="button.delete"/>',
				iconCls: 'delete',
				handler : this.deleteFile,
				scope : this
			},{
				id: 'moveUp' + this.id,
				text: '<fmt:message key="button.moveUp"/>',
				iconCls: 'Applicationget',
				scope: this,
				handler: this.moveUp
			},'-',{
				id: 'moveDown' + this.id,
				text: '<fmt:message key="button.moveDown"/>',
				iconCls: 'Applicationput',
				scope: this,
				handler: this.moveDown
			}]
		});
		
		this.imagePreviewPanel = Ext.create('Ext.Panel', {
			autoScroll: true,
			region: 'center',
			scope : this,
			html: this.scriptss
		});
		this.items = [this.fileList, this.imagePreviewPanel];
		this.callParent(arguments);
		
		this.gsm = this.fileList.getSelectionModel();
		this.fileList.on('cellclick', this.showImageClick, this);
    },

	showImageClick : function(grid, td, cellIndex, record){
		this.selectRecord = record;
		var src = record.get('filePath') + '?a=' + Date.parse(new Date());
		var fileType = record.get('fileType');
		var data = {src: src, fileType: fileType.toLowerCase()};
		var detailEl = this.imagePreviewPanel.body;
		var detailsTemplate = new Ext.XTemplate(
			'<div class="details" style="width: 300px; margin-top:10px; margin-left:5px;">'
				+ '<table>' 
					+ '<tr align=middle>'
						+ '<td align=middle>'
							+ '<tpl if="fileType==\'1\'||fileType==\'.jpg\'||fileType==\'.png\'||fileType==\'.gif\'||fileType==\'.jpeg\'">'
							+ '<img src="{src}" style="width:100%;"><div class="details-info">'
							+ '</tpl>'
							+ '<tpl if="fileType==\'2\'||fileType==\'.mp4\'||fileType==\'.3gp\'||fileType==\'.wmv\'||fileType==\'.flv\'">'
						    + '<video width="320" height="240"  controls="controls" autoplay><source src="{src}" type="video/mp4"><source src="{src}" type="video/ogg"></video>'
							
							+ '</tpl>'
						+ '</td>'
					+ '</tr>'
				+ '</table>'
			+ '</div>'
		);

		detailsTemplate.compile();
		detailEl.hide();
		detailsTemplate.overwrite(detailEl, data);
		detailEl.slideIn('l', {stopFx: true, duration: .2});
	},
	
    startUpload : function(){
    	this.store.each(function(record){								
			if(record.data.fileState == this.fileList.FILE_STATUS.QUEUED){
				var xhrObj = new XMLHttpRequest();
				xhrObj.open('POST', '<c:url value="/upload/fileMultiple"/>', true);
				xhrObj.upload.addEventListener("progress", (function(thiz, record){
					return function(evt){
						if (evt.lengthComputable) {
							var percentComplete = Math.round(evt.loaded / evt.total * 100);
							Ext.getDom('progressBar_' + record.data.fileId).style.width = percentComplete + "%";
							Ext.getDom('progressText_' + record.data.fileId).innerHTML = percentComplete + " %";
						}
					}
				})(this, record), false);
				
				xhrObj.upload.onprogress = (function(thiz, record){
					return function(evt){
						if (evt.lengthComputable) {
							var percentComplete = Math.round(evt.loaded / evt.total * 100);
							Ext.getDom('progressBar_' + record.data.fileId).style.width = percentComplete + "%";
							Ext.getDom('progressText_' + record.data.fileId).innerHTML = percentComplete + " %";
						}
					}
				})(this, record);

				xhrObj.onerror = (function(thiz, record){
					return function(evt){
						if (evt.lengthComputable) {
							record.data.fileState = thiz.fileList.FILE_STATUS.ERROR;
							record.commit();
						}
					}
				})(this, record);

				xhrObj.onreadystatechange = (function(thiz, record){
					return function(){
						if (xhrObj.readyState == 4 && xhrObj.status == 200 ) {
							var responseObject = Ext.JSON.decode(xhrObj.responseText);
							if(responseObject.success == true || responseObject.success == 'true'){
								record.data.fileState = thiz.fileList.FILE_STATUS.COMPLETE;
								record.data.filePath = responseObject.filePath;
								record.data.imageURL = responseObject.imageURL;
								record.data.status = 0;
								record.commit();
								Ext.getDom('progressBar_' + record.data.fileId).style.width = 100 + "%";
								Ext.getDom('progressText_' + record.data.fileId).innerHTML = 100 + " %";
							}else{
								record.data.fileState = thiz.fileList.FILE_STATUS.ERROR;
								record.commit();
								showFailMsg(responseObject.message, 4);
							}
						}
					}
				})(this, record);

				try{
					xhrObj.setRequestHeader("fileType", record.data.fileType);
					xhrObj.setRequestHeader("fileName", record.data.fileName);
					xhrObj.send(record.data.input_file); 
				}catch(exception){
					record.data.fileState = this.fileList.FILE_STATUS.ERROR;
					record.commit();
				}
			}				
		}, this)
    },
    
    deleteFile : function() {
		if (this.gsm.getSelection().length == 0){
			showWarnMsg('<fmt:message key="errors.noRecord"/>');
			return;
		}

		var records = this.gsm.getSelection();
        for(var i = 0 ; i < records.length ; i ++ ){			
			this.store.remove(records[i]);			
		}
	},
    
    progressFormat : function(_v, metaData, record){
    	var returnValue = '';
		switch (record.data.fileState) {
			case this.fileList.FILE_STATUS.COMPLETE :
				if (Ext.isIE) {
					returnValue = '<div class="x-progress-wrap" style="height: 18px">'
							+ '<div class="x-progress-inner">'
							+ '<div style="width: 100%;" class="x-progress-bar x-progress-text">' + '100 %'
					'</div>' + '</div>' + '</div>';
				} else {
					returnValue = '<div class="x-progress-wrap" style="height: 18px">'
							+ '<div class="x-progress-inner">' + '<div id="progressBar_' + record.data.fileId
							+ '" style="width: 100%;" class="x-progress-bar">' + '</div>' + '<div id="progressText_'
							+ record.data.fileId
							+ '" style="width: 100%;" class="x-progress-text x-progress-text-back" />100 %</div>'
					'</div>' + '</div>';
				}
				break;
			case this.fileList.FILE_STATUS.SUCCESS :
				returnValue = '<div style="height: 18px; color: red;"><b><fmt:message key="image.progress.success"/></b></div>';
				break;
			default :
				returnValue = '<div class="x-progress-wrap" style="height: 18px">' + '<div class="x-progress-inner">'
						+ '<div id="progressBar_' + record.data.fileId + '" style="width: 0%;" class="x-progress-bar">'
						+ '</div>' + '<div id="progressText_' + record.data.fileId
						+ '" style="width: 100%;" class="x-progress-text x-progress-text-back" />0 %</div>'
				'</div>' + '</div>';
				break;
		}
		return returnValue;
    },
    
    formatState : function(_v, cellmeta, record) {
		var returnValue = '';
		switch (_v) {
			case this.fileList.FILE_STATUS.QUEUED :
				returnValue = '<span><div id="fileId_' + record.data.fileId + '" class="multiple-wait"/>&nbsp;</span>';
				break;
			case this.fileList.FILE_STATUS.ERROR :
				returnValue = '<span><div id="fileId_' + record.data.fileId + '" class="multiple-error"/>&nbsp;</span>';
				break;
			case this.fileList.FILE_STATUS.COMPLETE :
				returnValue = '<span><div id="fileId_' + record.data.fileId + '" class="multiple-completed"/>&nbsp;</span>';
				break;
			case this.fileList.FILE_STATUS.SUCCESS :
				returnValue = '<span><div id="fileId_' + record.data.fileId + '" class="multiple-completed"/>&nbsp;</span>';
				break;
			default: break;
		}
		return returnValue;
	},

	moveUp : function(){
		var sRecords = this.gsm.getSelection();
		if (sRecords.length == 0) {
			return;
		}

		var s = this.fileList.store;
		var index = s.indexOf(sRecords[0]);
		if(index > 0)index = index - 1;
		for(var i = 0; i< sRecords.length; i++){
			s.remove(sRecords[i]);
		}
		s.insert(index, sRecords);
	},

	moveDown : function(){
		var sRecords = this.gsm.getSelection();
		if (sRecords.length == 0) {
			return;
		}
		
		var s = this.fileList.store;
		var index = s.indexOf(sRecords[0]);
		if(index + sRecords.length < s.getCount()) index = index + 1;
		for(var i = 0; i< sRecords.length; i++){
			s.remove(sRecords[i]);
		}
		s.insert(index, sRecords);	
	}
})
