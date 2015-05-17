var r = 960 / 2;

var cluster = d3.layout.cluster()
    .size([360, 1])
    .sort(null)
    .value(function(d) { return d.length; })
    .children(function(d) { return d.branchset; })
    .separation(function(a, b) { return 1; })

var bundle = d3.layout.bundle();

//var diagonal = d3.svg.diagonal.radial()
//    .projection(function(d) { return [d.y, d.x / 180 * Math.PI]; });

function deg2rad(angle) {
    return (angle / 180) * Math.PI;
}

function rad2deg(rad) {
    deg = (rad / Math.PI) * 180;
    if (deg < 0) {
        deg = 360 + deg;
    }
    return deg;
}

var diagonal = d3.svg.line.radial()
  .interpolate("cardinal")
  .tension(1)
  .radius(function(d) { return d.y; })
  .angle(function(d) { return deg2rad(d.x); });

var vis = d3.select("#chart").append("svg:svg")
    .attr("width", r * 2)
    .attr("height", r * 2 - 150)
  .append("svg:g")
    .attr("transform", "translate(" + r + "," + r + ")");

function phylo(n, offset) {
  if (n.data && n.data.length != null)
    offset += n.data.length * r;
  n.y = n.y * 50;
  if (n.children)
    n.children.forEach(function(n) {
      phylo(n, offset);
    });
}

function phylo_root(nodes) {
    nodes[0].children.forEach(function(n) {
      phylo_child(n, 0, 0, -1, -1);
    });
}

function phylo_child(n, p_off, p_off_trans, p_ang, p_ang_trans) {
  var c_off = n.y;
  var c_off_trans = n.data.length * r * 3;
  var c_ang = deg2rad(n.x);
  var c_ang_trans = -1;

  if (p_ang < 0) {
      c_ang_trans = c_ang;
  }
  else {
      //alert(n.data.name);
      //alert(p_off);
      //alert(rad2deg(p_ang));
      //alert(c_off);
      //alert(rad2deg(c_ang));
      ang_diff = Math.abs(c_ang - p_ang);
      //alert(rad2deg(ang_diff));
      off3 = Math.sqrt(Math.pow(p_off, 2) + Math.pow(c_off, 2) - (2 * p_off * c_off * Math.cos(ang_diff)));
      //alert(off3);
      ang2 = Math.acos((Math.pow(c_off, 2) - Math.pow(p_off, 2) - Math.pow(off3, 2)) / (-2 * p_off * off3))
      //alert(rad2deg(ang2));
      off3_trans = Math.sqrt(Math.pow(p_off_trans, 2) + Math.pow(c_off_trans, 2) - 
        (2 * p_off_trans * c_off_trans * Math.cos(ang2)));
      c_ang_trans = Math.acos((Math.pow(c_off_trans, 2) - Math.pow(p_off_trans, 2) - Math.pow(off3_trans, 2)) / (-2 * p_off_trans * off3_trans));
      c_ang_trans = c_ang > p_ang ? p_ang_trans + c_ang_trans : p_ang_trans - c_ang_trans;
      c_off_trans = off3_trans;
  }
  
  n.x = rad2deg(c_ang_trans);
  n.y = c_off_trans;
  
  if (n.children)
    n.children.forEach(function(c) {
      phylo_child(c, c_off, c_off_trans, c_ang, c_ang_trans);
    });
}

d3.text("tree.phyml", function(text) {
  var x = newick.parse(text);
  var nodes = cluster(x);
  //console.log(nodes);
  //alert(1);
  phylo_root(nodes);
  
  var link = vis.selectAll("path.link")
      .data(bundle(cluster.links(nodes)))
    .enter().append("svg:path")
      .attr("class", "link")
      .attr("d", diagonal);

  var node = vis.selectAll("g.node")
      .data(nodes)
    .enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })

  node.append("svg:circle")
      .attr("r", 4.5);

  node.append("svg:text")
      .attr("dx", function(d) { return d.x < 180 ? 8 : -8; })
      .attr("dy", ".31em")
      .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
      .attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
      .text(function(d) { return d.children ? '' : d.data.name.replace(/_/g, ' '); });
});
