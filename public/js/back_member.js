$(function(){


	var sideBarBox = $("#sideBarBox");//sidevar父容器


	$("#sidebarFoldBtn").on('click', function() {
		if ($(this).attr("title") === "展开") {
			$(this).attr("title","收起").parent().animate({left:0});
			$("#genericGroupManageDiv").animate({marginLeft:'411px'});
		} else {
			$(this).attr("title","展开").parent().animate({left:'-411px'})
			$("#genericGroupManageDiv").animate({marginLeft:'0px'});
		}
	});

	//获取初始根节点
	var getRootNode = function(){
		return $(sideBarBox).find(".genericGroupNode").eq(0);
	}

	//获取当前选中节点
	var getCurrNode = function(){
		return $(sideBarBox).find(".genericGroupNode.nav-on");
	}



	//选中节点
	sideBarBox.on('click',".genericGroupNode",function(){
		var ele = $(this);
		var currNode = getCurrNode();
		$.ajax({
			url:"/stem/switchGenericGroup",
			data:{
				groupId: ele.attr("data-id"),
				tbType:  ele.attr("data-table-type")
			},
			success: function(json){
				if(json.succ){
					currNode.removeClass("nav-on");
					ele.addClass("nav-on");
					$("#genericGroupManageDiv").html(json.html);
				}
			}
		});
	});


	$(".nav-class-box").on('click', '.ui-tree', function(e) {
		e.stopPropagation();
		$(this).parent().next().slideToggle('fast');
	}).on('click', '.nav', function() {
		if($(this).hasClass('nav-on')){
			$(this).find('.node-name').attr('contenteditable', true).focus();
		} else {
			$(this).parents('.bs-sidebar').find('.nav-on').removeClass('nav-on');
			$(this).addClass('nav-on');
		}
	}).on('blur', '.nav', function() {
		var ele=$(this);
		ele.find('.node-name').attr('contenteditable', false);
		var groupName=ele.find(".node-name").text().trim();
		$.ajax({
			url:"/stem/updateGroupName",
			data:{
				groupId: ele.attr("data-id"),
				groupName:groupName
			},
			success: function(json){
				if(json.resultStatus){
					layer.msg("修改成功！");
				}
			}
		});

	});;

	$('#createGeneric').on('click', function() {
		var labelName = $(this).text().trim();
		var currNode=getCurrNode();
		var data=currNode.attr("data-id");
		var cont =  '<div class="toper-form">'+
					'	<div class="form-group">'+
					'		<label class="lab" style="width:80px;">'+labelName+'名称</label>'+
					'		<div class="inb">'+
					'			<input class="ipt" type="text" id="groupName">'+
					'		</div>'+
					'	</div>'+
					'</div>';
		layer.open({
			type: 1,
			title: '新建'+labelName,
			area: ['420px', 'auto'],
			btn: ['确定', '取消'],
			content: cont,
			yes: function(index) {
				var groupName = $("#groupName").val();
				$.ajax({
					url:'/stem/addGenericGroup',
					contentType: "text/plain; charset=utf-8",
					data:{
						groupName:groupName,
						parentGroupId:data
					},
					success:function(data){
						if(data.resultStatus){
							var genericGroupId= data.data;
							var htmlStr='<div class="ml20"><div id="genericGroupNode_'+genericGroupId+'" class="nav cnode rel genericGroupNode " data-id="'+genericGroupId+'">'+
									   '	<span class="node-name">'+groupName+'</span>'+
									   '	<i class="i i-04 ui-tree"></i>'+
									   '	<i class="i i-08 ui-right ui-arrow"></i>'+
									   '</div></div>';
							currNode.parent().append(htmlStr);
							sideBarBox.find("#genericGroupNode_"+genericGroupId).click();
						}else{
							layer.msg(data.errorMsg);
						}
					}
				})
				layer.close(index);
			},
			no: function(index){
				layer.close(index);
			}
		});
	});


	$('#createClass').on('click', function() {
		var labelName = $(this).text().trim();
		var currNode=getCurrNode();
		var data=currNode.attr("data-id");
		var cont =  '<div class="toper-form">'+
					'	<div class="form-group">'+
					'		<label class="lab" style="width:80px;">'+labelName+'名称</label>'+
					'		<div class="inb">'+
					'			<input class="ipt" type="text" id="clazzName">'+
					'		</div>'+
					'	</div>'+
					'</div>';
		layer.open({
			type: 1,
			title: '新建'+labelName,
			area: ['420px', 'auto'],
			btn: ['确定', '取消'],
			content: cont,
			yes: function(index) {
				var clazzName = $("#clazzName").val();
				$.ajax({
					url:'/stem/addClazzGroup',
					data:{
						groupName:clazzName,
						parentGroupId:data
					},
					success:function(data){
						if(data.resultStatus){
							var genericGroupId= data.data;
							var htmlStr='<div class="ml20"><div id="genericGroupNode_'+genericGroupId+'" class="nav cnode rel genericGroupNode " data-id="'+genericGroupId+'">'+
									   '	<span class="node-name">'+clazzName+'</span>'+
									   '	<i class="i i-04 ui-tree"></i>'+
									   '	<i class="i i-08 ui-right ui-arrow"></i>'+
									   '</div></div>';
							currNode.parent().append(htmlStr);
							sideBarBox.find("#genericGroupNode_"+genericGroupId).click();
						}else{
							layer.msg(data.errorMsg);
						}
					}
				})
				layer.close(index);
			},
			no: function(index){
				layer.close(index);
			}
		});
	});



	//上移组织
	sideBarBox.on('click',"#upGenericGroup",function(){
		var currNode = getCurrNode();
		$.ajax({
			url:"/stem/upGroup",
			data:{
				groupId: currNode.attr("data-id")
			},
			success: function(json){
				if(json.resultStatus){
					currNode.parent().prev().before(currNode.parent());
				}else{
					layer.msg(json.errorMsg);
				}
			}
		});
	});

	//下移组织
	sideBarBox.on('click',"#downGenericGroup",function(){
		var currNode = getCurrNode();
		$.ajax({
			url:"/stem/downGroup",
			data:{
				groupId: currNode.attr("data-id")
			},
			success: function(json){
				if(json.resultStatus){
					currNode.parent().next().after(currNode.parent());
				}else{
					layer.msg(json.errorMsg);
				}
			}
		});
	});

	//删除组织
	sideBarBox.on('click',"#delGenericGroup",function(){
		var currNode = getCurrNode();
		layer.confirm('确定需要删除"'+ currNode.text().trim() +'"', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			$.ajax({
				url:"/stem/delGroup",
				data:{
					groupId: currNode.attr("data-id")
				},
				success: function(json){
					if(json.resultStatus){
						layer.msg("删除成功！")
						currNode.parent().remove();
						getRootNode().click();
					}else{
						layer.msg(json.errorMsg);
					}
				}
			});
		}, function(){

		});
	});






		/***********人员的增删改操作********************************************************************************************************/

		//全选 反选
		$("#genericGroupManageDiv").on('click','#checkAll',function(){
			var ele = $(this),isChecked = ele.prop("checked");
			$('input[name="id"]').each(function(index,item){
				$(item).prop("checked",isChecked);
			});
		})

		
		
		//删除
		$("#genericGroupManageDiv").on('click','#delMember',function(){
			var memberIds =[];
			$('input[name="id"]').each(function(index,item){
				if($(item).prop("checked")){
					memberIds.push($(item).attr("data-id"));
				};
			});
			if(memberIds.length==0){
				layer.msg("请选中用户再进行删除操作！");
			}else{
				layer.confirm('确认删除用户？', {
				  btn: ['确定','取消'] //按钮
				}, function(){
					$.ajax({
						url: "/stem/deleteGroupMembers",
						data:{
							groupId: getCurrNode().attr("data-id"),
							memberIds:memberIds
						},
						success:function(json){
							if(json.resultStatus){
								layer.msg("删除成功！");
								$(".memberTabSwitch.on").click();
							}else{
								layer.msg(json.errorMsg);
							}
						}
					})
				});
			}
		})
		
		//重置
		$("#genericGroupManageDiv").on('click','#resetPassword',function(){
			var memberIds =[];
			$('input[name="id"]').each(function(index,item){
				if($(item).prop("checked")){
					memberIds.push($(item).attr("data-id"));
				};
			});
			if(memberIds.length==0){
				layer.msg("请选中用户再进行删除操作！");
			}else{
				
				 var cont =  '<div class="toper-form">'+
			                 '   <div class="form-group">'+
			                 '       <label class="lab" style="width:80px;">新密码</label>'+
			                 '       <div class="inb">'+
			                 '           <input class="ipt" type="text" id="password">'+
			                 '       </div>'+
			                 '   </div>'+
			                 '</div>';

		         layer.open({
		             type: 1,
		             title: '重置密码',
		             area: ['420px', 'auto'],
		             btn: ['确定', '取消'],
		             content: cont,
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
		                     'margin':'0 30px 30px 92px',
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
		            	 var password = $("#password").val().trim();
		            	 if(password.length < 1){
		            		 layer.msg("密码不能为空!");
		            		 return false;
		            	 }
		            	 
		            	 $.post("/stem/resetPwdByMembers",{memberIds:memberIds,password:password},function(json){
		            		 if(json.resultStatus){
		            			 layer.close(index);
		            			 layer.msg("修改密码成功!");
		            			 
		            		 }
		            	 })
		            	 
		             },
		             no: function(index){
		                 layer.close(index);
		             }
		         });
				
				
				
				
			}
		})
		
		
		
		
		//导入教师

		$("#genericGroupManageDiv").on('click','#importMember',function(){
			var url = '/stem/importGroupMember',
				downloadUrl = '/Application/downloadTemplate?templateName=template.xls';
			var groupId= getCurrNode().attr("data-id");
			var cont =  '<div class="toper-form">'+
						'	<form action="'+ url +'" method="POST" enctype="multipart/form-data" id="fileForm" name="fileForm">'+
						'		<input type="file" name="file">'+
						'		<input type="hidden" name="groupId" value="'+groupId+'">'+
						'		<a href="'+ downloadUrl +'">导入模版下载</a>'+
						'	</form>'+
						'</div>';

			layer.open({
				type: 1,
				title: '导入成员',
				area: ['420px', 'auto'],
				btn: ['确定', '取消'],
				content: cont,
				yes: function(index) {
					var form = $("form[name=fileForm]");
					 var options  = {
			            type:'post',
			            success:function(json){
			            	if(json.status){
			            		layer.close(index);
    							if(json.data.trim().length>0){
    								layer.confirm(json.data, {
									  btn: ['确定','取消'] //按钮
									}, function(){
									}, function(){
									});
    							}
    							$(".genericGroupNode.nav-on").click();

    						}
			            }
			        };
			        form.ajaxSubmit(options);

				},
				no: function(index){
					layer.close(index);
				}
			});

		})
		//添加成员
		$("#genericGroupManageDiv").on('click','#addMember',function(){
			var ele=$(this),groupId= getCurrNode().attr("data-id");
			var cont =  '<div class="toper-form" id="addMemberBox">'+
			'	<div class="form-group">'+
			'		<label class="lab" style="width:80px;">工号</label>'+
			'		<div class="inb">'+
			'			<input class="ipt" type="text">'+
			'		</div>'+
			'	</div>'+
			'	<div class="form-group">'+
			'		<label class="lab" style="width:80px;">姓名</label>'+
			'		<div class="inb">'+
			'			<input class="ipt" type="text">'+
			'		</div>'+
			'	</div>'+
			'	<div class="form-group">'+
			'		<label class="lab" style="width:80px;">邮箱</label>'+
			'		<div class="inb">'+
			'			<input class="ipt" type="text">'+
			'		</div>'+
			'	</div>'+
			'	<div class="form-group">'+
			'		<label class="lab" style="width:80px;">手机</label>'+
			'		<div class="inb">'+
			'			<input class="ipt" type="text">'+
			'		</div>'+
			'	</div>'+
			'	<div class="form-group">'+
			'		<label class="lab" style="width:80px;">职位</label>'+
			'		<div class="inb">'+
			'			<input class="ipt" type="text">'+
			'		</div>'+
			'	</div>'+
			'</div>';
			layer.open({
				type: 1,
				title: '添加成员',
				area: ['420px', 'auto'],
				btn: ['确定', '取消'],
				content: cont,
				yes: function(index) {
					var inputArray = $("#addMemberBox input");
					if(isEmpty(inputArray.eq(0).val().trim(),"不能为空！"))return false;
					if(isEmpty(inputArray.eq(1).val().trim(),"不能为空！"))return false;
					if(isEmpty(inputArray.eq(2).val().trim(),"不能为空！"))return false;
					//if(!isEmail(inputArray.eq(2).val().trim())){layer.msg("邮箱格式不正确！");return false;}
					if(isEmpty(inputArray.eq(3).val().trim(),"不能为空！"))return false;
					if(!isMobil(inputArray.eq(3).val().trim())){layer.msg("手机格式不正确！");return false;}
					if(isEmpty(inputArray.eq(4).val().trim(),"不能为空！"))return false;
					$.ajax({
						url: "/stem/addGroupMember",
						data:{
							groupId: getCurrNode().attr("data-id"),
							"person.number":inputArray.eq(0).val().trim(),
							"person.fullName":inputArray.eq(1).val().trim(),
							"person.email":inputArray.eq(2).val().trim(),
							"person.cellPhone":inputArray.eq(3).val().trim(),
							"person.job":inputArray.eq(4).val().trim()
						},
						success:function(json){
							if(json.resultStatus){
								layer.msg("添加成功！");
								$(".genericGroupNode.nav-on").click();
								layer.close(index);
							}else{
								layer.msg(json.errorMsg);
							}
						}
					})

				},
				no: function(index){
					layer.close(index);
				}
			});

		})
		
		
		
		
		
		
		$("#genericGroupManageDiv").on("click",".js-set-personInfo",function(){
			var personId = $(this).attr("data-id"),
				number = $(this).attr("data-number"),
				fullName = $(this).attr("data-name"),
				cellPhone = $(this).attr("data-phone"),
				job = $(this).attr("data-job");
			var cont =  '<div class="toper-form" id="addMemberBox">'+
						'	<div class="form-group">'+
						'		<label class="lab" style="width:80px;">工号</label>'+
						'		<div class="inb">'+
						'			<input class="ipt" type="text" id="number" value="'+number+'">'+
						'		</div>'+
						'	</div>'+
						'	<div class="form-group">'+
						'		<label class="lab" style="width:80px;">姓名</label>'+
						'		<div class="inb">'+
						'			<input class="ipt" type="text" id="fullName" value="'+fullName+'">'+
						'		</div>'+
						'	</div>'+
						'	<div class="form-group">'+
						'		<label class="lab" style="width:80px;">手机</label>'+
						'		<div class="inb">'+
						'			<input class="ipt" type="text" id="cellPhone" value="'+cellPhone+'">'+
						'		</div>'+
						'	</div>'+
						'	<div class="form-group">'+
						'		<label class="lab" style="width:80px;">职位</label>'+
						'		<div class="inb">'+
						'			<input class="ipt" type="text" id="job" value="'+job+'">'+
						'		</div>'+
						'	</div>'+
						'</div>';
			layer.open({
				type: 1,
				title: '修改信息',
				area: ['420px', 'auto'],
				btn: ['确定', '取消'],
				content: cont,
				yes: function(index) {
					number = $("#number").val().trim();
					fullName = $("#fullName").val().trim();
					cellPhone = $("#cellPhone").val().trim();
					job = $("#job").val().trim();
					$.post("/stem/updatePersonInfo",{personId:personId,number:number,fullName:fullName,cellPhone:cellPhone,job:job},function(json){
						if(json.resultStatus){
							window.location.reload();
						}else{
							layer.msg(json.errorMsg);
						}
					})
				
				},
				no: function(index){
					layer.close(index);
				}
			});

		})

		


});

