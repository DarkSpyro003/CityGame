<?php
require_once 'question.class.php';
class GameContent
{
	private $questionList = array();
	
	public GameContent($questionList)
	{
		$this->questionList = $questionList;
	}
}
?>