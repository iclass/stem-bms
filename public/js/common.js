$(function() {
	
	
	  $('body').on('click','.reset-password', function() {
	        var cont =  '<div class="toper-form">'+
	                    '   <div class="form-group">'+
	                    '       <label class="lab" style="width:80px;">旧密码</label>'+
	                    '       <div class="inb">'+
	                    '           <input class="ipt" type="password" id="pass">'+
	                    '       </div>'+
	                    '   </div>'+
	                    '   <div class="form-group">'+
	                    '       <label class="lab" style="width:80px;">新密码</label>'+
	                    '       <div class="inb">'+
	                    '           <input class="ipt" type="password" id="newPass">'+
	                    '       </div>'+
	                    '   </div>'+
	                    '   <div class="form-group">'+
	                    '       <label class="lab" style="width:80px;">确认密码</label>'+
	                    '       <div class="inb">'+
	                    '           <input class="ipt" type="password" id="confirmPass">'+
	                    '       </div>'+
	                    '   </div>'+
	                    '</div>';
	        layer.open({
	            type: 1, 
	            title: ['重置密码'],
	            shade: 0.3,
	            area: ['420px', 'auto'],
	            content: cont,
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
	            	var pass = $("#pass").val().trim(),
	            		newPass = $("#newPass").val().trim(),
	            		confirmPass = $("#confirmPass").val().trim();
	            	if(newPass != confirmPass){
	            		layer.msg("新密码不一致！");
	            		return;
	            	}
	            	$.post("/stem/resetPassword",{pass:pass,newPass:newPass},function(json){
	            		if(json.resultStatus){
	            			layer.close(index);
	            		}else{
	            			layer.msg(json.errorMsg);
	            		}
	            	})
	            	
	                
	            }
	        });
	    });
	
	
	
	
	
	
	
	
	
});
