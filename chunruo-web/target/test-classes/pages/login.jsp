<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
 	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <title><fmt:message key="webapp.name"/></title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
	<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="${ctx }/jquery/bootstrap.min.css" />
    <link rel="stylesheet" href="${ctx }/jquery/fonts/font-awesome.min.css" />
    <link rel="stylesheet" href="${ctx }/jquery/login.css">
    
</head>
 
<body>
	<div id="loginHeight">
	<div class="alert alert-warning" id="notice" style="display:none">
	<a href="#" class="close" data-dismiss="alert">
		&times;
	</a>
	<strong id="warnning"></strong>
</div>
	<span class="verticalAlign"></span>
	<div class="login_wrap">
        <h2 class="login_title"><fmt:message key="webapp.name"/></h2>
        <form id="loginForm" action="" class="form-horizontal" method="post">
        	<input type="hidden" id="exponent" value="${publicKeyExponent}"/>
        	<input type="hidden" id="modulus" value="${publicKeyModulus}"/>
            <div class="form-group mb20">
                <label for="name" class="sr-only">Name</label>
                <div class="col-sm-12">
                    <div class="input-group">
                        <div class="input-group-addon"><i class="fa fa-user fs20 w20"></i></div>
                        <input type="text" class="form-control h42" id="username" name="username" placeholder="<fmt:message key="login.username"/>">
                    </div>
                    <div class="error"></div>
                </div>
            </div>
            <div class="form-group mb20">
                <label for="password" class="sr-only">Password</label>
                <div class="col-sm-12">
                    <div class="input-group">
                        <div class="input-group-addon"><i class="fa fa-lock fs20 w20"></i></div>
                        <input type="password" class="form-control h42" id="password" name="password" placeholder="<fmt:message key="login.password"/>">
                    </div>
                    <div class="error"></div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-7">
                    <div class="input-group keystyle">
                        <div class="input-group-addon"><i class="fa fa-check fs20 w20"></i></div>
                        <input class="form-control h42" id="code" name="code" maxlength="6" placeholder="<fmt:message key="login.validate"/>" >
                    </div>
                    <div class="error"></div>
                </div>
                <div class="col-sm-5" style="margin-top:6px;">
             <input type="button" id="btn" class="btn btn-success" value="<fmt:message key="login.code"/>"></input>
                 </div>
            </div>
            
             <div class="form-group">
                <div class="col-sm-7">
                    <div class="input-group">
            <div style="display: flex;flex-direction: row;justify-content: flex-start;"><input type="checkbox" name="remember-me" id="rememberMe"  style = "width:20px;height:20px" /><font style="height: 23px;line-height: 28px;margin-left: 10px;" color="blue"><fmt:message key="auto.login.next"/></font></div>
                    </div>
                    <div class="error"></div>
                </div>
            </div>
           
            <div class="form-group">
                <div class="col-sm-12">
                    <a id="submit" type="button" class="btn btn-success btn-block" role="button"><fmt:message key="login.title"/></a>
                </div>
            </div>
        </form>
	</div>
	</div>
	
	<script type="text/javascript">
		var ctx = '${ctx }';
		window.jQuery || document.write("<script src='${ctx }/jquery/jquery-2.1.3.min.js'>"+"<"+"/script>");
	</script>
	<script type="text/javascript" src='${ctx }/jquery/plugins/validate/jquery.validate.min.js'></script>
	<script type="text/javascript" src='${ctx }/jquery/plugins/validate/additional-methods.js'></script>
	<script type="text/javascript" src='${ctx }/jquery/plugins/validate/localization/messages_zh.js'></script>
	<script type="text/javascript" src='${ctx }/jquery/plugins/RSA.js'></script>
	<script type="text/javascript" src='${ctx }/jquery/login.js'></script>
</body>
</html>
