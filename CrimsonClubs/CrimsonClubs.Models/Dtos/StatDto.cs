﻿using CrimsonClubs.Models.Entities;
using CrimsonClubs.Models.Enums;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CrimsonClubs.Models.Dtos
{
    public class StatDto
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string Abbreviation { get; set; }
        public StatType Type { get; set; }

        public StatDto()
        {

        }

        public StatDto(Stat dbo)
        {
            Id = dbo.Id;
            Name = dbo.Name;
            Description = dbo.Description;
            Abbreviation = dbo.Abbreviation;
            Type = (StatType)dbo.Type;
        }
    }
}
