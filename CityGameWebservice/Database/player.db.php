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
	
	public function createPlayer($player, $password)
	{
		if( $statement = $this->database->prepare('INSERT INTO `players` (`username`, `passwordhash`, `email`, `realname`) VALUES (?, ?, ?, ?)') )
		{
			$username = $this->database->real_escape_string($player->username);
			$email = $this->database->real_escape_string($player->email);
			$realname = $this->database->real_escape_string($player->realname);
			$passwordhash = $this->createPasswordHash($password);
			
			$statement->bind_param('ssss', $username, $passwordhash, $email, $realname);
			$statement->execute();
			return $this->database->affected_rows;
		}
		else
			return 0;
	}
	
	public function createPasswordHash($password)
	{
		return password_hash($password, PASSWORD_BCRYPT);
	}
	
	public function completeQuestions($playerId, $questiondata) 
	{
		$queryString = 'INSERT INTO `player_question` (`pid`, `gid`, `qid`, `answer`) VALUES ';
		$first = true;
		foreach($questiondata as $data)
		{
			if( $first )
			{
				$first = false;
			}
			else
			{
				$queryString .= ',';
			}
			$gid = $this->database->real_escape_string($data['gid']);
			$qid = $this->database->real_escape_string($data['qid']);
			$answer = $this->database->real_escape_string($data['answer']);
			$queryString .= "($playerId, $gid, $qid, '$answer')";
		}
		$this>database->query($queryString);
	}
	
	public function completeGameContent($username, $gameContentId, $score, $questiondata)
	{
		$player = $this->getPlayerByUsername($username);
		if( !$this->hasCompletedGameContent($player->id, $gameContentId) )
		{
			if( $statement = $this->database->prepare('INSERT INTO `player_games` (`playerId`, `gameContentId`, `score`) VALUES (?, ?, ?)') )
			{
				$playerId = $this->database->real_escape_string($player->id);
				$gameContentId = $this->database->real_escape_string($gameContentId);
				$score = $this->database->real_escape_string($score);
				
				$statement->bind_param('iid', $playerId, $gameContentId, $score);
				$statement->execute();
				if( $this->database->affected_rows > 0 )
				{
					$statement->close();
					completeQuestions($playerId, $questiondata);
					return 201;
				}
				else
				{
					$statement->close();
					return 500;
				}
			}
			else
			{
				return 500;
			}
		}
		else
		{
			return 409;
		}
	}
	
	public function hasCompletedGameContent($playerId, $gameContentId)
	{
		$playerId = $this->database->real_escape_string($playerId);
		$gameContentId = $this->database->real_escape_string($gameContentId);
		
		$gamesResult = $this->database->query('SELECT `playerId`, `gameContentId`, `score` FROM `player_games` WHERE `playerId` = ' . $playerId . ' AND `gameContentId` = ' . $gameContentId);
		return $gamesResult->num_rows > 0;
	}
	
	public function updatePlayer($player, $oldusername, $password, $oldpassword)
	{
		// Check if player exists in DB
		if( !is_null(getPlayerByUsername($player->username)) )
		{
			// Player exists, check password
			if( checkPassword($oldusername, $oldpassword) )
			{
				if( $statement = $this->database->prepare('UPDATE `players` SET `username` = ?, `passwordhash` = ?, `email` = ?, `realname` = ? WHERE `username` = ?') )
				{
					
					$username = $this->database->real_escape_string($player->username);
					$email = $this->database->real_escape_string($player->email);
					$realname = $this->database->real_escape_string($player->realname);
					$passwordhash = $this->createPasswordHash($password);
					$oldusername = $this->database->real_escape_string($oldusername);
			
					$statement->bind_param('sssss', $username, $passwordhash, $email, $realname, $oldusername);
					$statement->execute();
					if( $this->database->affected_rows > 0 )
					{
						$statement->close();
						return 200;
					}
					else
					{
						$statement->close();
						return 500;
					}
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
			if( createPlayer($player, $password) > 0 )
				return 201;
			else
				return 500;
		}
	}
	
	public function checkPassword($username, $password)
	{
		$username = $this->database->real_escape_string($username);
		
		$playerResult = $this->database->query('SELECT `passwordhash` FROM `players` WHERE `username` = \'' . $username . '\'');
		$passwordhash = $playerResult->fetch_assoc()['passwordhash'];
		return password_verify($password, $passwordhash);
	}
	
	public function deletePlayerByUsername($username)
	{
		$username = $this->database->real_escape_string($username);
		
		$result = $this->database->query('DELETE FROM `players` WHERE `username` = \'' . $username . '\'');
		return $this->database->affected_rows;
	}
	
	public function getPlayerByUsername($username)
	{
		$username = $this->database->real_escape_string($username);
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