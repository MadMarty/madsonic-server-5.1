var renderer;
var splashScene;

var camera;
var camTarget;

var oceanMeshMaterial;
var oceanUniforms;
var oceanMaterial;

var ocean;
var oceanPlane;
var oceanWidth = 3000 *3;
var oceanLength = 3000 *3;
var oceanDivisions = 30 *3;

var container;
var background;

var splashClock;

var opVertPos, opVerts;
var opColVertPos, opColVerts;

var waveHeightA = 265;
var waveSpeedA = 7.1;
var waveOffsetA = 1.2834448552536923;

var waveHeightB = 0.01;
var waveSpeedB = 2.96;
var waveOffsetB = 2.3;

var aa_target = new THREE.Scene();

var wavesPlaying = false;

var aa_available = true;

function start()
{
	
	if ( ! Detector.webgl ) {
		
		Detector.addGetWebGLMessage();
	}

	container = document.createElement( 'div' );
	document.body.appendChild( container );
	
	renderer = new THREE.WebGLRenderer({antialias: true});

	aa_test();

	renderer.setSize(window.innerWidth, window.innerHeight);	
	container.appendChild(renderer.domElement);

	splashScene = new THREE.Scene();
	splashScene.dynamic = true;

	oceanMeshMaterial = new THREE.MeshBasicMaterial( { color: 0xcccccc, wireframe: true, transparent: true, opacity: 1.0 } );
	blackMaterial = new THREE.MeshBasicMaterial( { color: 0x111111, wireframe: false, transparent: false, opacity: 1.0 } );


	oceanUniforms = {
		u_color : { type: "c", value: new THREE.Color( 0x383a49 ) }
	};
		
	oceanMaterial = new THREE.ShaderMaterial( {

		uniforms: oceanUniforms,
		vertexShader: document.getElementById( 'oceanSurfaceVert' ).textContent,
		fragmentShader: document.getElementById( 'oceanSurfaceFrag' ).textContent

	} );

	oceanMaterial.wireframe = true;
	
	oceanPlane = new THREE.PlaneGeometry(oceanWidth, oceanLength, oceanDivisions, oceanDivisions);
	oceanPlane.doubleSided = true;
	oceanPlane.dynamic = true;
	oceanPlane.computeFaceNormals();
	oceanPlane.computeVertexNormals();

	ocean = new THREE.Mesh(oceanPlane, oceanMaterial);
	ocean.name = name +"_ocean";
	ocean.rotation.x = Math.PI/2;
	ocean.doubleSided = true;
	
	splashScene.add(ocean);
	
	loadCamera();

	background = new THREE.Color();
	background.r = 0.05;
	background.g = 0.05;
	background.b = 0.05;
	renderer.setClearColor(background, 1.0);
	renderer.clear();

	splashClock = new THREE.Clock();
	splashClock.start();
	waveTime = 0;

	wavesPlaying = true;

	animate();
}


function aa_test() {
	renderer.setSize(4, 4);

	var ortho_camera =  new THREE.OrthographicCamera(0, 4, 0, 4, 0, 1 );
	var output = new Uint8Array( 4 );

	var material = new THREE.LineBasicMaterial({
    color: 0xffffff
  });

	var geometry = new THREE.Geometry();
	geometry.vertices.push(new THREE.Vector3(0, 0, 0));
	geometry.vertices.push(new THREE.Vector3(4, 4, 0));

  var line = new THREE.Line(geometry, material);

  renderer.clearTarget( aa_target.renderTarget, true, true, true );
  renderer.context.lineWidth(4);

	aa_target.add(line);
	renderer.render( aa_target, ortho_camera, aa_target.renderTarget );

	renderer.context.readPixels( 0, 2, 1, 1, renderer.context.RGBA, renderer.context.UNSIGNED_BYTE, output );

	if (output[0] == 0)
		aa_available = false;
    
}

function loadCamera() {
	
	camTarget = new THREE.Mesh(new THREE.Geometry(), blackMaterial);	
	camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 1, 10000 );

	camera.position.x = -338;
	camera.position.y = -48;
	camera.position.z = 4000;

	camera.rotation.x = 0;
	camera.rotation.y = 0;
	camera.rotation.z = 0;

	camTarget.position.x = 500;
	camTarget.position.y = -467;
	camTarget.position.z = 0

	camera.lookAt(camTarget.position);

	splashScene.add(camera);
}

function animate() {

if(wavesPlaying){

	if(splashClock != undefined){
		var t = splashClock.getElapsedTime();
		
		waves(waveTime * 0.012);
		
		renderer.clear();
		renderer.render( splashScene, camera );

		
			waveTime++;
			window.requestAnimationFrame(animate, renderer.domElement);
		}
	}
	
}


function waves(t) {

	//big waves
	opVerts = oceanPlane.vertices;

	
	var len = opVerts.length;
	for ( var i = 0; i < len; i ++ )
	{
		opVerts[i].z = this.waveA(opVerts[i].x, opVerts[i].y, t );	
	}
	

	var waveVar;
	var colorWave;
	
	//small waves
	for ( var j = 0, l = this.oceanPlane.vertices.length; j < l; j ++ ) {		
		oceanPlane.vertices[ j ].z = oceanPlane.vertices[ j ].z + waveB(this.oceanPlane.vertices[j].x, oceanPlane.vertices[j].z, t);
	}

	ocean.geometry.__dirtyVertices = true;
	ocean.geometry.verticesNeedUpdate = true;
}

function waveA (x, y, t) {
	return Math.sin( ( x / 20 ) * waveOffsetA + ( t / waveSpeedA ) ) * Math.cos( ( y / 20 ) * waveOffsetA + ( t / waveSpeedA ) ) * waveHeightA;
}

function waveB (x, y, t) {
	return Math.sin( ( x / 2 ) * waveOffsetB + ( t / waveSpeedB ) ) * Math.cos( ( y / 2 ) * waveOffsetB + ( t / waveSpeedB ) ) * waveHeightB;
}

//generic mpping of a value from one range to another
function remap (value, initStart, initEnd, finalStart, finalEnd) {
	mapped = (( (value - initStart) *(finalEnd - finalStart) ) / (initEnd- initStart)) + finalStart;
	return mapped;
}

window.addEventListener( 'resize', onWindowResize, false );


function onWindowResize() {
	
	//playWaves();
  setTimeout(function() {	
		windowHalfX = window.innerWidth / 2;
		windowHalfY = window.innerHeight / 2;

		camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		renderer.setSize( window.innerWidth, window.innerHeight );
	}, 10);
}

function pauseWaves(){
	////console.log("pauseWaves");
	wavesPlaying = false;
}

function playWaves(){
	////console.log("playWaves");
	wavesPlaying = true;	
	animate();
}


