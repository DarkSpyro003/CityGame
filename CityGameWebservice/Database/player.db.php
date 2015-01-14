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
	
	public function createPlayer($player, $passwordhash)
	{
		if( $statement = $this->database->prepare('INSERT INTO `players` (`username`, `passwordhash`, `email`, `realname`) VALUES (?, ?, ?, ?)') )
		{
			$statement->bind_param('ssss', $player->username, $passwordhash, $player->email, $player->realname);
			$statement->execute();
			return $this->database->affected_rows;
		}
		else
			return 0;
	}
	
	public function updatePlayer($player, $oldusername, $passwordhash, $newpasswordhash)
	{
		// Check if player exists in DB
		if( !is_null(getPlayerByUsername($player->username)) )
		{
			// Player exists, check password
			if( checkPassword($passwordhash) )
			{
				if( $statement = $this->database->prepare('UPDATE `players` SET `username` = ?, `passwordhash` = ?, `email` = ?, `realname` = ? WHERE `username` = ?') )
				{
					$statement->bind_param('sssss', $player->username, $newpasswordhash, $player->email, $player->realname, $oldusername);
					$statement->execute();
					if( $this->database->affected_rows > 0 )
						return 200;
					else
						return 500;
				}
				else
					return 500;
				
			}
			else // Not authorized
			{
				return 401;
			}
		}
		else // Player being updated doesn't exist, create instead
		{
			if( createPlayer($player, $passwordhash) > 0 )
				return 201;
			else
				return 500;
		}
	}
	
	public function checkPassword($passwordhash)
	{
		throw new Exception('Not yet implemented');
		return false;
	}
	
	public function deletePlayerByUsername($username)
	{
		$result = $this->database->query('DELETE FROM `players` WHERE `username` = \'' . $username . '\'');
		return $this->database->affected_rows;
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
				while($game = $gamesResult->fetch_assoc())
				{
					$games[] = new PlayedGame($game['gameContentId'], $game['score']);
				}
				$player = new Player($row['username'], $row['email'], $row['realname']);
				$player->id = $playerId;
				$player->games = $games;
				
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