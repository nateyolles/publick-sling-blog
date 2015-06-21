<%@page session="false" %><%
%><%@page import="org.apache.sling.api.resource.ValueMap" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%

    final ValueMap attributes = resource.getValueMap();
    final String title = attributes.get("testproperty", "not found");
%><html>
  <head>
    <title></title>
  </head>
  <body class="ui-slingshot-main">
  <div>
    testproperty = <%= title %>
  </div>
</body>
</html>