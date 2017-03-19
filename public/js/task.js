$(function() {
	
	
	//新建任务
	$(".main").on("click",".add-task",function(){
		
		$.post("/stem/jsonAddTask",{},function(json){
			if(json.succ){
				layer.open({
		            type: 1, 
		            title: ['新建任务', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
		            shade: 0.3,
		            area: ['710px', 'auto'],
		            content: json.html,
		            btn: ['确定','取消'],
		            success: function (layero) {
		                var btn = layero.find('.layui-layer-btn0');
		                btn.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#21ABE9',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 30px 30px 240px',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		                var btn1 = layero.find('.layui-layer-btn1');
		                btn1.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#999',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 0 30px 0',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		            },
		            yes: function(index) {
		            	var groupId = $("#chooseGroup").val(),
		            		taskName = $("#taskName").val().trim();
		            	if(taskName.length < 1){
		            		layer.msg("任务名称不能为空！");
		            		return false;
		            	}
		            	$.post("/stem/addTask",{groupId:groupId,name:taskName},function(json){
		            		if(json.status ="true"){
		            			  layer.close(index);
		            			  window.location.reload();
		            		}
		            		
		            	})
		            	
		              
		            }
				});

			}
			
		})
		
	})
	
	
	
	
	//左侧点击任务
	$(".js-task-list").on("click","li",function(){
		var taskId = $(this).attr("data-id");
		$(this).parent().parent().parent().find("li").removeClass("selected");
		$(this).addClass("selected");
		$.post("/stem/jsonTask",{taskId:taskId},function(json){
			if(json.succ){
				$(".main-right").html(json.html);
			}
			
		})
	})
	
	//右侧点击tab页
	$(".main-right").on("click","li",function(){
		var taskId = $(this).parent().attr("data-id"),
			type = $(this).attr("data-type");
		$(this).addClass("selected").siblings().removeClass("selected");
		
		$.ajax({
			 url: '/stem/jsonTaskTab' ,
 		     type: 'POST',
 		     data:{taskId:taskId,type:type},
    	   	 beforeSend:function(){
    	   		$(".mainright-all").html('<div class="loading-wrap"><div class="ld-img"></div><div class="ld-txt">正在加载中......</div></div>');
    			$('.loading-wrap').show(); 
		     },
		    success: function (json) {
		    	$('.loading-wrap').hide(); 
		    	$(".mainright-all").html(json.html);
		    }
			
		})
		
	})
	
	
	
	
	//分配经理
	$(".main-right").on("click",".js-allot-manager",function(){
		var curNode = $(this);
		var tpId = $(this).parents("tr").attr("data-id");
		var type = $(this).attr("data-type");
		$.post("/stem/jsonTaskPerson",{type:type},function(json){
			if(json.succ){
				layer.open({
		            type: 1, 
		            title: ['分配', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
		            shade: 0.3,
		            area: ['710px', 'auto'],
		            content: json.html,
		            btn: ['确定','取消'],
		            success: function (layero) {
		                var btn = layero.find('.layui-layer-btn0');
		                btn.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#21ABE9',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 30px 30px 240px',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		                var btn1 = layero.find('.layui-layer-btn1');
		                btn1.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#999',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 0 30px 0',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		            },
		            yes: function(index) {
		            	var personId = $("#choosePerson").val(),
		            		personName = $("#choosePerson").find("option:selected").text();
		            	$.post("/stem/relateManager",{tpId:tpId,personId:personId},function(json){
		            		if(json.status ="true"){
		            			  layer.close(index);
		            			  curNode.prev().text("");
		            			  curNode.prev().text(personName);
		            		}
		            		
		            	})
		            	
		              
		            }
				});
				
				
				
				$("#chooseGroup").on("change",function(){
					var groupId = $("#chooseGroup").val(),
						type = $("#type").val();
					$.post("/stem/jsonChooseDept",{groupId:groupId,type:type},function(json){
						if(json.succ){
							$("#choosePerson").html(json.html);
						}
					})
					
				})
				
				
				
			}
		})
	})
	
	
	
	
	
	//分配顾问
	$(".main-right").on("click",".js-allot-adviser",function(){
		var curNode = $(this);
		var tpId = $(this).parents("tr").attr("data-id");
		var type = $(this).attr("data-type");
		$.post("/stem/jsonTaskPerson",{type:type},function(json){
			if(json.succ){
				layer.open({
		            type: 1, 
		            title: ['分配', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
		            shade: 0.3,
		            area: ['710px', 'auto'],
		            content: json.html,
		            btn: ['确定','取消'],
		            success: function (layero) {
		                var btn = layero.find('.layui-layer-btn0');
		                btn.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#21ABE9',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 30px 30px 240px',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		                var btn1 = layero.find('.layui-layer-btn1');
		                btn1.css({
		                    'text-align': 'center',
		                    'width': '90px',
		                    'color': '#FFF',
		                    'height': '32px',
		                    'background': '#999',
		                    'display': 'block',
		                    'float': 'left',
		                    'margin':'0 0 30px 0',
		                    'line-height':'30px',
		                    'border-radius':'15px',
		                    'font-size':'16px'
		                });
		            },
		            yes: function(index) {
		            	var personId = $("#choosePerson").val(),
	            			personName = $("#choosePerson").find("option:selected").text();
		            	$.post("/stem/relateAdviser",{tpId:tpId,personId:personId},function(json){
		            		if(json.status ="true"){
		            			  layer.close(index);
		            			  curNode.prev().text("");
		            			  curNode.prev().text(personName);
		            		}
		            		
		            	})
			            	
		              
		            }
				});
				
				
				$("#chooseGroup").on("change",function(){
					var groupId = $("#chooseGroup").val(),
						type = $("#type").val();
					$.post("/stem/jsonChooseDept",{groupId:groupId,type:type},function(json){
						if(json.succ){
							$("#choosePerson").html(json.html);
						}
					})
					
				})
				
				
			}
		})
	})
	
	
	
	
	$(".main-right").on("click",".js-uploadExcel",function(){
		var tpId = $(this).parents("tr").attr("data-id");
		$("#uploadFile").click();
		$("#tpId").val(tpId);
		$("#fileType").val("excel");
	})
	
	$(".main-right").on("click",".js-uploadFormula",function(){
		var tpId = $(this).parents("tr").attr("data-id");
		$("#uploadFile").click();
		$("#tpId").val(tpId);
		$("#fileType").val("formula");
	})
	
	$(".main-right").on("click",".js-uploadPdf",function(){
		var tpId = $(this).parents("tr").attr("data-id");
		$("#uploadFile").click();
		$("#tpId").val(tpId);
		$("#fileType").val("pdf");
	})
	
	$(".main-right").off("change").on("change","#uploadFile",function(){
		var formData = new FormData($("#uploadForm")[0]);
		$.ajax({
		    url: '/application/uploadResource' ,
		    type: 'POST',
		    data: formData,
		    async: false,
		    cache: false,
		    contentType: false,
		    processData: false,
		    beforeSend:function(){
		    	 layer.load(3, {
	        		  shade: [0.1,'#fff'] //0.1透明度的白色背景
		    	 });
		    },
		    success: function (json) {
		    	
			    if(json.status == "true"){
			    	var type=$("#fileType").val();
			    	if(type == "excel"){
			    		$.post("/stem/uploadInterviewRecord",{tpId:$("#tpId").val(),url:json.oneResult.html,name:json.oneResult.name},function(result){
				    		if(result.status == "true"){
				    			layer.closeAll();
				    			layer.msg("上传成功！");
				    			$(".js-task-list").find("li.selected").trigger("click");
				    		}
				    	})
			    	}
			    	
			    	if(type == "formula"){
			    		$.post("/stem/uploadFormula",{tpId:$("#tpId").val(),url:json.oneResult.html,name:json.oneResult.name},function(result){
				    		if(result.status == "true"){
				    			layer.closeAll();
				    			layer.msg("上传成功！");
				    			$(".js-task-list").find("li.selected").trigger("click");
				    			
				    		}
				    	})
			    		
			    	}
			    	
			    	if(type == "pdf"){
			    		$.post("/stem/uploadEvaluateRecord",{tpId:$("#tpId").val(),url:json.oneResult.html,name:json.oneResult.name},function(result){
				    		if(result.status == "true"){
				    			layer.closeAll();
				    			layer.msg("上传成功！");
				    			$(".js-task-list").find("li.selected").trigger("click");
				    			
				    		}
				    	})
			    		
			    	}
			    }else{
			    	var errorTips = json.data;
			    	layer.closeAll();
			    	layer.open({
			            type: 1, 
			            title: ['错误信息', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
			            shade: 0.3,
			            area: ['600px', '500px'],
			            content: errorTips,
		   
			            })
			    	return false;
			    }
		    
		    	
		    	
		    },
		    error: function (data) {
		        alert(data);
		    }
		});
		
		
		
	})
	
	
	
	//记录审查
	$(".main-right").on("click",".js-record-evalute",function(){
		var tpId = $(this).parents("tr").attr("data-id");
		var curNode = $(this);
		var state ;
		if(curNode.find("i").hasClass("icon-closev-active")){
			state = false;
		}
		if(curNode.find("i").hasClass("icon-trues-active")){
			state = true;
		}
		
		
		$.post("/stem/evaluteRecord",{tpId:tpId,state:state},function(json){
			if(json.status == "true"){
				if(curNode.find("i").hasClass("icon-closev-active")){
					curNode.find("i").addClass("red");
					curNode.siblings().find("i").removeClass("green");
				}
				if(curNode.find("i").hasClass("icon-trues-active")){
					curNode.find("i").addClass("green");
					curNode.siblings().find("i").removeClass("red");
				}
			}
			
		})
	})
	
	
	//报告审查
	$(".main-right").on("click",".js-report-evalute",function(){
		var tpId = $(this).parents("tr").attr("data-id");
		var curNode = $(this);
		var state ;
		if(curNode.find("i").hasClass("icon-closev-active")){
			state = false;
		}
		if(curNode.find("i").hasClass("icon-trues-active")){
			state = true;
		}
		
		$.post("/stem/evaluteReport",{tpId:tpId,state:state},function(json){
			if(json.status == "true"){
				if(curNode.find("i").hasClass("icon-closev-active")){
					curNode.find("i").addClass("red");
					curNode.siblings().find("i").removeClass("green");
				}
				if(curNode.find("i").hasClass("icon-trues-active")){
					curNode.find("i").addClass("green");
					curNode.siblings().find("i").removeClass("red");
				}
				
				//更新总表
				$.post("/stem/MergeFile",{tpId:tpId,state:state},function(result){
					if(result.status == "true"){
						layer.msg("总表更新成功！请刷新查看！")
					}
				})
				
				
				
			}else{
				layer.msg("您无权修改审查状态！");
				return false;
			}
			
		})
	})
	
	
	//标题排序
	 $('body').on('click', '.presentation', function(event) {
		 var sortType,orderType=$(this).attr("data-type"),taskId = $(this).parents("table").attr("data-id");
	        event.preventDefault();
	        if($(this).hasClass('sx-on')){
	            $('.presentation').removeClass('sx-on');
	            $(this).addClass('sx-off').siblings().removeClass('sx-off');
	            sortType = "desc"
	            
	        }else {
	            $('.presentation').removeClass('sx-off');
	            $(this).addClass('sx-on').siblings().removeClass('sx-on');
	            sortType = "asc"
	        }
	        $.post("/stem/jsonTask",{taskId:taskId,orderType:orderType,sortType:sortType},function(json){
	        	if(json.succ){
					$(".main-right").html(json.html);
				}
	        	
	        })

	    });
	
	
	
	
	
	//总表下载
	$(".main-right").on("click",".zongbiao-btn",function(){
		var url = $(this).attr("data-url");
		if($(this).parent().hasClass("grey")){
			layer.msg("您没有权限下载总表");
			return false;
		}
		if(url.length == ""){
			layer.msg("总表没有生成！");
			return false;
		}
		window.location.href = "/stem/download?fileUrl="+url +"&fileName="+"Quarterly MR score report.xlsx";
		
	})
	
	//总表刷新
	$(".main-right").on("click",".refresh",function(){
		if($(this).parent().hasClass("grey")){
			layer.msg("您没有权限刷新总表");
			return false;
		}
		var taskId = $(this).attr("data-id");
		
		$.ajax({
		    url: '/stem/batchMergeFiles',
		    type: 'POST',
		    data: {taskId:taskId},
		    beforeSend:function(){
		    	 layer.load(3, {
	        		  shade: [0.1,'#fff'] //0.1透明度的白色背景
		    	 });
		    },
		    success: function (result) {
		    	if(result.status == "true"){
		    		layer.closeAll();
					layer.msg("总表刷新成功！");
					setTimeout(function(){
						window.location.reload();
					},1000)
				}
		    }
		    
		})
		
	})
	
	
	
});
