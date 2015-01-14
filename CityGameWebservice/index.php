<?php
// Include the Slim framework
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();

// MySQL database access:
require_once 'Config/config.php';
$database = new mysqli($dbhost, $dbuser, $dbpassword, $dbname);

// Objects used
require_once 'Database/gamecontent.class.php';

// Application routing:
// Game content data -- GET
$app->get(
    '/gamecontent/:id',
    function ($id) use ($database, $app)
	{
		require_once 'Database/gamecontent.db.php';
        $gamecontentdb = new GameContentDb($database);
		$content = $gamecontentdb->getGameContentById($id);
		if( is_null($content) )
		{
			$app->response()->status(404);
			echo '404 Object Not Found';
		}
		else
			echo json_encode($content);
    }
);

require_once 'Database/player.db.php';
// PLAYER CRUD
// GET route
$app->get(
    '/player/:username',
    function ($username) use ($database, $app) 
	{
        $playerdb = new PlayerDb($database);
		$content = $playerdb->getPlayerByUsername($username);
		if( is_null($content) )
		{
			$app->response()->status(404);
			echo '404 Object Not Found';
		}
		else
			echo json_encode($content);
    }
);

// POST route -- Create
$app->post(
    '/user/post',
    function () use ($database, $app)
	{
		$app->response()->status(201);
		$app->response->headers->set('Location', $newUrl); // Holds GET url to the created resource
    }
);

// PATCH route -- Update
$app->patch('/user/patch',
	function () 
	{
		echo 'This is a PATCH route';
	}
);

// DELETE route -- Delete
$app->delete(
    '/user/delete',
    function () 
	{
        echo 'This is a DELETE route';
    }
);

// Run the Slim Framework application
$app->run();
$database->close();
?>