﻿using CrimsonClubs.Models;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;

namespace CrimsonClubs.Controllers.Api
{
    [RoutePrefix("api/club")]
    public class ClubController : CCApiController
    {
        [HttpGet, Route]
        [ResponseType(typeof(Club[]))]
        public IHttpActionResult GetUserClubs()
        {
            int userId = 1;

            var clubs = new List<Club>();

            string sql = "SELECT c.Id, c.Name, c.Description, ISNULL(g.Name,'') AS GroupName, ( SELECT COUNT(*) FROM MM_User_Club WHERE ClubId = c.Id ) AS MemberCount FROM [User] u JOIN MM_User_Club m ON u.Id = m.UserId JOIN Club c ON c.Id = m.ClubId LEFT JOIN [Group] g ON g.Id = c.GroupId WHERE u.Id = @UserId;";
            using (var conn = new SqlConnection(ConnectionString))
            using (var cmd = new SqlCommand(sql, conn))
            {
                cmd.Parameters.AddWithValue("@UserId", userId);

                conn.Open();

                var reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    var club = new Club();
                    club.Id = (int)reader["Id"];
                    club.Name = (string)reader["Name"];
                    club.Description = (string)reader["Description"];
                    club.GroupName = (string)reader["GroupName"];
                    club.MemberCount = (int)reader["MemberCount"];

                    clubs.Add(club);
                }
            }

            return Ok(clubs);
        }

        [HttpGet, Route("{clubId}/calendar")]
        [ResponseType(typeof(Event[]))]
        public IHttpActionResult GetClubCalendar(int clubId)
        {
            var events = new List<Event>();

            string sql = "SELECT e.Id, e.Name, e.Description, e.Start, e.Finish, e.IsGroupEvent, c.Id AS ClubId, c.Name AS ClubName FROM Club c JOIN MM_Club_Event m2 ON m2.ClubId = c.Id JOIN [Event] e ON e.Id = m2.EventId WHERE c.Id = @ClubId;";
            using (var conn = new SqlConnection(ConnectionString))
            using (var cmd = new SqlCommand(sql, conn))
            {
                cmd.Parameters.AddWithValue("@ClubId", clubId);

                conn.Open();

                var reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    var e = new Event();
                    e.Id = (int)reader["Id"];
                    e.Name = (string)reader["Name"];
                    e.Description = (string)reader["Description"];
                    e.Start = (DateTime)reader["Start"];
                    e.Finish = (DateTime)reader["Finish"];
                    e.IsGroupEvent = (bool)reader["IsGroupEvent"];
                    e.ClubId = (int)reader["ClubId"];
                    e.ClubName = (string)reader["ClubName"];

                    events.Add(e);
                }
            }

            return Ok(events);
        }
    }
}
