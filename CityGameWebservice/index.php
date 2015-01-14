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
			echo '404 Resource Not Found';
		}
		else
			echo json_encode($content);
    }
);

// POST route -- Create
$app->post(
    '/player/create',
    function () use ($database, $app, $serviceroot)
	{
		$username = $app->$request->post('username');
		$passwordhash = $app->$request->post('passwordhash');
		$email = $app->$request->post('email');
		$realname = $app->$request->post('realname');
		
        $playerdb = new PlayerDb($database);
		$content = $playerdb->getPlayerByUsername($username);
		if( is_null($content) )
		{
			$player = new Player($username, $email, $realname);
			if ( $playerdb->createPlayer($player, $passwordhash) > 0 )
			{
				$app->response()->status(201);
				$newUrl = $serviceroot . '/player/' . $username;
				$app->response->headers->set('Location', $newUrl); // Holds GET url to the created resource
			}
			else
			{
				$app->response()->status(500);
				echo '500 Internal Server Error - Something went wrong';
			}
		}
		else
		{
			$app->response()->status(409);
			echo '409 Resource Already Exists - The requested username is already taken';
		}
    }
);

// PUT route -- Create & Update
$app->put('/player/:username',
	function ($username) use ($database, $app, $serviceroot)
	{
		$player;$passwordhash; // TODO : implement
		throw new Exception('Not yet implemented');
        $playerdb = new PlayerDb($database);
		$status = $playerdb->updatePlayer($player, $passwordhash);
		$app->response()->status($status);
		if( $status == 201 )
		{
			$newUrl = $serviceroot . '/player/' . $username;
			$app->response->headers->set('Location', $newUrl); // Holds GET url to the created resource
		}
		else if( $status == 401 )
			echo '401 Unauthorized';
		else if( $status == 500 )
			echo '500 Internal Server Error - Something went wrong';
	}
);

// DELETE route -- Delete
$app->delete(
    '/player/:username',
    function ($username) use ($database, $app)
	{
        $playerdb = new PlayerDb($database);
		$content = $playerdb->getPlayerByUsername($username);
		if( is_null($content) )
		{
			$app->response()->status(404);
			echo '404 Resource Not Found';
		}
		else
		{
			$passwordhash = $app->request->params('passwordhash');
			if( $playerdb->checkPassword($passwordhash) )
			{
				if( $playerdb->deletePlayerByUsername($username) > 0 )
				{
					$app->response()->status(200);
				}
			}
			else
			{
				// Wrong password!
				$app->response()->status(401);
				echo '401 Unauthorized';
			}
		}
    }
);

// Run the Slim Framework application
$app->run();

// Close the database connection
$database->close();
?>