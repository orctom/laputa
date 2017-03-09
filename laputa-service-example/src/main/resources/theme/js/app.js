var connect = function(onMessage) {
  var socket = new SockJS('/metrics-websocket');
  var stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
    stompClient.subscribe('/topic/metrics', function(message) {
      onMessage(JSON.parse(message.body));
    });
    stompClient.send("/app/metrics");
  });
};

var basicType = {
  connector: "StateMachine",
  paintStyle: {
    stroke: "blue",
    strokeWidth: 4
  },
  hoverPaintStyle: {
    stroke: "blue"
  },
  overlays: [
    "Arrow"
  ]
};

var randomColor = function() {
  return '#'+(0x1000000+(Math.random())*0x0fffff).toString(16).substr(1,6);
};

var normalize = function(name) {
  return name.replace(/[^0-9a-zA-Z_-]/g, "_");
}

jsPlumb.ready(function() {
  var instance = jsPlumb.getInstance({
    ConnectionOverlays: [
      ["Arrow", {
        location: 0.1,
        visible: true,
        foldback: 0.7,
        fill: "blue",
        width: 11,
        stroke: "blue",
        length: 11
      }],
      ["Label", {
        location: 0.5,
        id: "label",
        stroke: "blue",
        cssClass: "aLabel"
      }],
      ["Arrow", {
        location: 0.7,
        visible: true,
        foldback: 0.7,
        fill: "blue",
        width: 11,
        stroke: "blue",
        length: 11
      }],
      ["Arrow", {
        location: 1,
        visible: true,
        foldback: 0.7,
        fill: "blue",
        width: 11,
        stroke: "blue",
        length: 11
      }],
    ],
    ReattachConnections: true,
    Endpoints: ["Blank","Blank"],
    Connector: "Bezier",
    Anchor: ["Continuous", { faces:[ "left", "right" ] } ],
    Container: "canvas"
  });

  instance.registerConnectionType("basic", basicType);

  var updateLabel = function(connection, label) {
    connection.getOverlay("label").setLabel(label);
  };

  var updateLayout = function() {
    var $canvas = $("#canvas");
    var canvasWidth = $canvas.outerWidth();

    var x = 0, y = 50, xSpan = 250, ySpan = 180;
    $canvas.find(".group-container").each(function() {
      var $group = $(this);
      var groupWidth = $group.outerWidth();
      var top = y + 'px';
      var left = x + 'px';
      $group.css({left:left,top:top});

      x += (groupWidth + xSpan);
      if (x + groupWidth >= canvasWidth) {
        y += ($group.outerHeight() + ySpan);
        x = 0;
      }
    });

    instance.repaintEverything();
  };

  var processMessage = function(message) {
    var type = message.type;
    var key = message.key;
    var field = message.field;
    var value = message.value;
    switch(type) {
      case "ENDPOINT": {
        processEndpoint(key, value);
        break;
      }
      case "CONNECTION": {
        processConnection(key, field, value);
        break;
      }
      case "METER": {
        if ("up" == field) {
          processEndpoint(key, value);
        } else if ("down" == field) {
          deleteEndpoint(key, value);
        } else {
          processMeter(key, field, value);
        }
        break;
      }
    }
  };

  var processEndpoint = function(applicationName, roles) {
    var normalizedApplicationName = normalize(applicationName);
    var endpoints = applications[normalizedApplicationName];
    if (endpoints) {
      return;
    }
    endpoints = applications[normalizedApplicationName] = {};
    var $group = $("#template-application").clone().prop("id", normalizedApplicationName);
    $group.find(".title").text(applicationName);
    $group.appendTo("#canvas");

    _.each(roles.split(","), function(role) {
      var roleId = normalizedApplicationName + "-" + role;
      if (endpoints[roleId]) {
        return;
      }
      $endpoint = $("#template-role").clone().prop("id", roleId);
      $endpoint.find(".role").text(role);
      $endpoint.appendTo($group);

      endpointsWithEmptyMeters[roleId] = role;
      endpoints[roleId] = role;

      if (rolesMapping[role]) {
        rolesMapping[role].push(roleId);
      } else {
        rolesMapping[role] = [roleId];
      }
    });

    instance.draggable($group);
    updateLayout();
  };

  var processConnection = function(source, prefix, targetRoles) {
    if (!_.isEmpty(endpointsWithEmptyMeters)) {
      return;
    }

    var sourceRoleId = prefix + "-" + source;
    _.each(targetRoles.split(","), function(targetRole) {
      _.each(rolesMapping[targetRole], function(targetRoleId) {
        var literal = sourceRoleId + "-" + targetRoleId;
        if (connectionLiterals[literal]) {
          return;
        }

        instance.connect({
          source: sourceRoleId,
          target: targetRoleId,
          paintStyle: { stroke: randomColor(), startsWith: 3 },
        });
      });
    });
  };

  var processMeter = function(roleId, meter, value) {
    if ("sent" == meter) {
      var conns = connections[roleId];
      if (conns) {
        _.each(_.values(conns), function(connection) {
          updateLabel(connection, value);
        });
      }
    } else {
      var meter_key = roleId + "-" + meter;
      if (meters[meter_key]) {
        $("#" + roleId + " ." + meter).text(value);
      } else {
        var $meter = $("#template-meter").clone().removeAttr("id");
        $meter.find(".badge").text(value).addClass(meter);
        $meter.find(".meter").text(meter);
        $meter.appendTo("#" + roleId + " > .list-group");
        meters[meter_key] = meter_key;

        delete endpointsWithEmptyMeters[roleId];
      }
    }
  };

  var deleteEndpoint = function(applicationName, roles) {
    var normalizedApplicationName = normalize(applicationName);
    deleteApplication(normalizedApplicationName);
    deleteConnections(normalizedApplicationName, roles);
    deleteMeters(normalizedApplicationName, roles);
  };

  var deleteApplication = function(normalizedApplicationName) {
    $("#" + normalizedApplicationName).remove();
    delete applications[normalizedApplicationName];
  };

  var deleteConnections = function(normalizedApplicationName, roles) {
    for (var sourceRole in connections) {
      var conns = connections[sourceRole];
      _.each(roles.split(","), function(role) {
        var roleId = normalizedApplicationName + "-" + role;
        if (sourceRole == roleId) {
          for (var targetRole in conns) {
            var connection = conns[targetRole];
            if (connection) {
              instance.detach(connection);
            }
            deleteConnectionLiterals(sourceRole, targetRole);
          }
          delete connections[sourceRole];
        } else {
          for (var targetRole in conns) {
            var connection = conns[targetRole];
            if (targetRole == roleId) {
              instance.detach(connection);
              delete conns[targetRole];
              deleteConnectionLiterals(sourceRole, targetRole);
            }
          }
        }
      });
    }
  };

  var deleteConnectionLiterals = function(sourceRoleId, targetRoleId) {
    var literal = sourceRoleId + "-" + targetRoleId;
    delete connectionLiterals[literal];
  };

  var deleteMeters = function(normalizedApplicationName, roles) {
    _.each(roles.split(","), function(role) {
      var roleId = normalizedApplicationName + "-" + role;
      for(var key in meters) {
        if (key.startsWith(roleId)) {
          delete meters[key];
        }
      }
    });
  };

  var applications = {};
  var endpointsWithEmptyMeters = {};
  var rolesMapping = {};
  var connections = {};
  var connectionLiterals = {};
  var meters = {};
  instance.bind("connection", function (connInfo, originalEvent) {
    var sourceId = connInfo.sourceId;
    var targetId = connInfo.targetId;
    var connection = connInfo.connection;
    var conns = connections[sourceId];
    if (conns) {
      conns[targetId] = connection;
    } else {
      connections[sourceId] = {};
      connections[sourceId][targetId] = connection;
    }

    var literal = sourceId + "-" + targetId;
    connectionLiterals[literal] = literal;
  });

  connect(function(message) {
    if (Array.isArray(message)) {
      for (var i in message) {
        var msg = message[i];
        processMessage(msg);
      }
    } else {
      processMessage(message);
    }
  });
});
