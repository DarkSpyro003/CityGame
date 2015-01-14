<?php
require_once 'gamecontent.class.php';
require_once 'question.db.php';

class GameContentDb
{
	private $database;
	
	public function __construct($database)
	{
		$this->database = $database;
	}
	
	public function getGameContentById($id)
	{
		$result = $this->database->query('SELECT `id`, `title` FROM `gamecontent` WHERE `id` = ' . $id);
		if( $result->num_rows > 0 )
		{
			$questiondb = new QuestionDb($this->database);
			$row = $result->fetch_assoc();
			$cId = $row['id'];
			$questionList = $questiondb->getQuestionListForContent($cId);
			$gamecontent = new GameContent($row['title'], $questionList);
			
			return $gamecontent;
		}
		else
		{
			throw new Exception('GameContent with id ' . $id . ' was not found.');
		}
	}
}
?>