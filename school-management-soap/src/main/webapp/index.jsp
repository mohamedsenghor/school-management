<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sectors & Classes SOAP Web Services</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .service { background: #f5f5f5; padding: 20px; margin: 20px 0; border-radius: 5px; }
        .service h3 { color: #333; margin-top: 0; }
        .endpoint { color: #007acc; text-decoration: none; }
        .endpoint:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>Sectors & Classes SOAP Web Services</h1>
    <p>Bienvenue sur l'interface des services web SOAP pour la gestion des secteurs et classes.</p>

    <div class="service">
        <h3>Sectors Web Service</h3>
        <p><strong>Endpoint:</strong> <a href="sectorsWebService" class="endpoint">sectorsWebService</a></p>
        <p><strong>WSDL:</strong> <a href="sectorsWebService?wsdl" class="endpoint">sectorsWebService?wsdl</a></p>
        <p>Gestion des secteurs d'activité (CRUD complet, recherche, statistiques)</p>
    </div>

    <div class="service">
        <h3>Classes Web Service</h3>
        <p><strong>Endpoint:</strong> <a href="classesWebService" class="endpoint">classesWebService</a></p>
        <p><strong>WSDL:</strong> <a href="classesWebService?wsdl" class="endpoint">classesWebService?wsdl</a></p>
        <p>Gestion des classes par secteur (CRUD complet, filtrage, recherche)</p>
    </div>

</body>
</html>
