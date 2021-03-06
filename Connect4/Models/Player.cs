﻿using System;

namespace Connect4.Models
{
    public class Player
    {

        public Player(string teamName)
        {
            this.Name = teamName;
        }

        public Player(string teamName, Guid playerID)
        {
            this.Name = teamName;
            this.ID = playerID;
        }

        public Guid? CurrentGameID { get; set; }
        public Guid ID { get; set; }
        public string Name { get; set; }
        public string Password { get; internal set; }
        public bool SystemBot { get; set; }

        public string WebHook { get; set; }
    }
}