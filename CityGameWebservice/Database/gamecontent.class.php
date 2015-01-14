<?php
require_once 'question.class.php';

class GameContent
{
	private $title;
	private $questionList = array();
	
	public function __construct($title, $questionList)
	{
		$this->title = $title;
		$this->questionList = $questionList;
	}
	
	public function getTitle()
	{
		return $this->title;
	}
	
	public function setTitle($title)
	{
		$this->title = $title;
	}
}
?>