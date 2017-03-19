$(function() {

  
    $('.mainright-top').on('click', 'li', function() {
            $(this).addClass('selected').siblings().removeClass('selected');
    });

    $('body').on('click','.add-task', function() {
        layer.open({
            type: 1, 
            title: ['新建任务', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
            shade: 0.3,
            area: ['710px', 'auto'],
            content: $('.layer-add'),
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
                layer.close(index);
            }
        });
    }); 
    $('body').on('click','.reset-password', function() {
        var cont =  '<div class="toper-form">'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">旧密码</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="password">'+
                    '       </div>'+
                    '   </div>'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">新密码</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="password">'+
                    '       </div>'+
                    '   </div>'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">确认密码</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="password">'+
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
                layer.close(index);
            }
        });
    });

    $('body').on('click','.fenpei-btn', function() {
        layer.open({
            type: 1, 
            title: ['分配经理', 'color:#394557;font-size:16px;border-bottom:1px solid #C8C8C8'],
            shade: 0.3,
            area: ['710px', '432px'],
            content: $('.layer-fenpei'),
            className:'fenpei1',
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
                layer.close(index);
            }
        });
    });
    $('#createGeneric').on('click', function() {
        var cont =  '<div class="toper-form">'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">年级(系)名称</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="text">'+
                    '       </div>'+
                    '   </div>'+
                    '</div>';
        layer.open({
            type: 1,
            title: '新建部门',
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
                layer.close(index);
            },
            no: function(index){
                layer.close(index);
            }
        });
    });

    $('#createClass').on('click', function() {
        var cont =  '<div class="toper-form">'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">班级名称</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="text">'+
                    '       </div>'+
                    '   </div>'+
                    '</div>';
        layer.open({
            type: 1,
            title: '新建区域',
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
                layer.close(index);
            },
            no: function(index){
                layer.close(index);
            }
        });
    });

    $('.t-option-bar').on('click', '.reset-pass', function() {
        var ele = $(this).parents('#genericGroupManageDiv').find("input:checked");

        if (!ele.length) {
            layer.alert('请先选择一个用户');
        } else {
            var cont =  '<div class="toper-form">'+
                    '   <div class="form-group">'+
                    '       <label class="lab" style="width:80px;">新密码</label>'+
                    '       <div class="inb">'+
                    '           <input class="ipt" type="text">'+
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
                    layer.close(index);
                },
                no: function(index){
                    layer.close(index);
                }
            });
        }
    }).on('click', '.del-member', function() {
        var ele = $(this).parents('#genericGroupManageDiv').find("input:checked");

        if (!ele.length) {
            layer.alert('请先选择一个用户');
        } else {
            layer.confirm('确定要删除选中用户吗？', {
                btn: ['确定','取消']
            }, function(index){
                ele.parents('tr').remove();
                layer.close(index); 
            }, function(index){
                layer.close(index); 
            });
        }
    })

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
        $(this).find('.node-name').attr('contenteditable', false)
    });;
    
    $("#sidebarFoldBtn").on('click', function() {
        if ($(this).attr("title") === "展开") {
            $(this).attr("title","收起").parent().animate({left:0});
            $("#genericGroupManageDiv").animate({marginLeft:'411px'});
        } else {
            $(this).attr("title","展开").parent().animate({left:'-411px'})
            $("#genericGroupManageDiv").animate({marginLeft:'0px'});
        }
    });

    $('body').on('click', '.presentation', function(event) {
        event.preventDefault();
        if($(this).hasClass('sx-on')){
            $('.presentation').removeClass('sx-on');
            $(this).addClass('sx-off').siblings().removeClass('sx-off');
        }else {
            $('.presentation').removeClass('sx-off');
            $(this).addClass('sx-on').siblings().removeClass('sx-on');
        }

    });
    $('.mainright-head').on('click', '.grey', function(event) {
        event.preventDefault();
        layer.msg('您未授权！');   
    });

    $('.download-table .green').attr('title','通过提交!');

});
