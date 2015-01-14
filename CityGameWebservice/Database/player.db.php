<?php
require_once('player.class.php');
require_once('playedgame.class.php');

class PlayerDb
{
	private $database;
	
	public function __construct($database)
	{
		$this->database = $database;
	}
	
	public function getPlayerByUsername($username)
	{
		$result = $this->database->query('SELECT `id`, `username`, `email`, `realname` FROM `players` WHERE `username` = \'' . $username . '\'');
		
		if( $result->num_rows > 0 )
		{
			while($row = $result->fetch_assoc())
			{
				$playerId = $row['id'];
				$games = array();
				$gamesResult = $this->database->query('SELECT `playerId`, `gameContentId`, `score` FROM `player_games` WHERE `playerId` = ' . $playerId);
				while($game = $result->fetch_assoc())
				{
					$games[] = new PlayedGame($game['gameContentId'], $game['score']);
				}
				$player = new Player($playerId, $row['username'], $row['email'], $row['realname'], $games);
				
				return $player;
			}
		}
		else
		{
			return null;
		}
	}
}
?>