<?php
require_once 'question.class.php';
class GameContent
{
	private $title;
	private $questionList = array();
	
	public GameContent($title)
	{
		$this->title = $title;
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