/*
 * Copyright (c) 2018 uturista.pt
 *
 * Licensed under the Attribution-NonCommercial 4.0 International (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.uturista.prspy.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Html;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.uturista.flags.Country;
import pt.uturista.flags.World;


public class Server implements Parcelable {
    private final InetSocketAddress Address;
    private final Country Country;
    private final String ServerName;
    private final String MapName;
    @DataTypes.GameModes
    private final String GameMode;
    @DataTypes.GameLayers
    private final int GameLayer;
    private final int NumPlayers;
    private final int MaxPlayers;
    private final int ReservedSlots;
    private final boolean Password;
    private final String OS;
    private final boolean BattleRecorder;
    private final String ServerText;
    private final Uri ServerLogo;
    private final String Team1Name;
    private final String Team2Name;
    private final Player[] Players;
    private final String Description;

    public Server(pt.uturista.bf2.Server json) throws UnknownHostException {
        Address = new InetSocketAddress(Inet4Address.getByName(json.IPAddress), json.GamePort);

        // Country
        Country c = World.WORLD_MAP.get(json.Country.toLowerCase());
        if(c == null)
            c = World.UNITED_NATIONS;
        Country = c;

        // ServerName, MapName and GameMode
        ServerName = Html.fromHtml(json.ServerName.replaceAll("\\[PR\\sv[\\d.]*\\]\\s", "")).toString();
        MapName = Html.fromHtml(json.MapName).toString();
        GameMode = DataTypes.getGameMode(json.GameMode);

        // GameLayer, NumPlayers, MaxPlayers and ReservedSlots
        GameLayer = DataTypes.getGameLayer(json.MapSize);
        NumPlayers = json.NumPlayers;
        MaxPlayers = json.MaxPlayers;
        ReservedSlots = json.ReservedSlots;

        // Password, OS, BattleRecorder and ServerText
        Password = json.Password;
        OS = json.OS;
        BattleRecorder = json.BattleRecorder;
        ServerText = json.ServerText;

        // ServerLogo, Team1Name and Team2Name
        ServerLogo = Uri.parse(json.ServerLogo);
        Team1Name = json.Team1Name;
        Team2Name = json.Team2Name;


        // Players and Description
        Players = new Player[json.Players.length];
        for (int i = 0; i < json.Players.length; i++) {
            Players[i] = new Player(json.Players[i], getID());
        }
        Description = json.ServerText.replace("|", System.getProperty("line.separator", "\n"));
    }


    /* *********************************
     *         Equals Region
     * ********************************* */

    @Override
    public int hashCode() {
        return Address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Server other = (Server) obj;
        return this.Address.equals(other.Address) && this.getServerName().equals(other.getServerName());
    }


    /* *********************************
     *         Getters Region
     * ********************************* */

    public InetSocketAddress getAddress() {
        return Address;
    }

    @NonNull
    public Country getCountry() {
        return Country;
    }

    public String getServerName() {
        return ServerName;
    }

    public String getMapName() {
        return MapName;
    }

    @DataTypes.GameModes
    public String getGameMode() {
        return GameMode;
    }

    @DataTypes.GameLayers
    public int getGameLayer() {
        return GameLayer;
    }

    public int getNumPlayers() {
        return NumPlayers;
    }

    public int getMaxPlayers() {
        return MaxPlayers;
    }

    public int getReservedSlots() {
        return ReservedSlots;
    }

    public boolean hasPassword() {
        return Password;
    }

    public String getOS() {
        return OS;
    }

    public boolean hasBattleRecorder() {
        return BattleRecorder;
    }

    public String getServerText() {
        return ServerText;
    }

    public Uri getServerLogo() {
        return ServerLogo;
    }

    public String getTeamName(@DataTypes.Teams int team) {
        switch (team) {
            case DataTypes.Blufor:
                return Team1Name;
            case DataTypes.Opfor:
                return Team2Name;
            default:
                return Team1Name;
        }
    }

    @NonNull
    public Player[] getPlayers(@DataTypes.Teams int team) {
        ArrayList<Player> players = new ArrayList<>(Players.length/2);

        for (Player p : Players) {
            if (p.getTeam() == team)
                players.add(p);
        }

        Player[] ret = new Player[players.size()];
        return players.toArray(ret);
    }

    @NonNull
    public Player[] getPlayers() {
        return Players;
    }

    public boolean hasFriends() {
        for (Player player : Players) {
            if (player.isFriend()) {
                return true;
            }
        }

        return false;
    }


    public String getDescription() {
        return Description;
    }

    public String getID() {
        return Address.toString();
    }


    /* *********************************
     *         Parcelable Region
     * ********************************* */
    protected Server(Parcel in) {
        // Custom serialization of Address (Host and Port)
        String hostname = in.readString();
        int port = in.readInt();
        Address =  new InetSocketAddress(hostname, port);

        // Country
        Country c = World.WORLD_MAP.get(in.readString());
        if(c == null)
            c = World.UNITED_NATIONS;
        Country = c;

        // ServerName, MapName and GameMode
        ServerName = in.readString();
        MapName = in.readString();
        GameMode = DataTypes.getGameMode(in.readString());

        // GameLayer, NumPlayers, MaxPlayers and ReservedSlots
        GameLayer = DataTypes.getGameLayer(in.readInt());
        NumPlayers = in.readInt();
        MaxPlayers = in.readInt();
        ReservedSlots = in.readInt();

        // Password, OS, BattleRecorder and ServerText
        Password = in.readByte() != 0;
        OS = in.readString();
        BattleRecorder = in.readByte() != 0;
        ServerText = in.readString();

        // ServerLogo, Team1Name and Team2Name
        ServerLogo = Uri.parse(in.readString());
        Team1Name = in.readString();
        Team2Name = in.readString();

        // Players and Description
        Players = in.createTypedArray(Player.CREATOR);
        Description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Custom serialization of Address (Host and Port)
        dest.writeString(Address.getAddress().getHostAddress());
        dest.writeInt(Address.getPort());

        // Country
        dest.writeString(Country.CODE_2);

        // ServerName, MapName and GameMode
        dest.writeString(ServerName);
        dest.writeString(MapName);
        dest.writeString(GameMode);

        // GameLayer, NumPlayers, MaxPlayers and ReservedSlots
        dest.writeInt(GameLayer);
        dest.writeInt(NumPlayers);
        dest.writeInt(MaxPlayers);
        dest.writeInt(ReservedSlots);

        // Password, OS, BattleRecorder and ServerText
        dest.writeByte((byte) (Password ? 1 : 0));
        dest.writeString(OS);
        dest.writeByte((byte) (BattleRecorder ? 1 : 0));
        dest.writeString(ServerText);

        // ServerLogo, Team1Name and Team2Name
        dest.writeString(ServerLogo.toString());
        dest.writeString(Team1Name);
        dest.writeString(Team2Name);

        // Players and Description
        dest.writeTypedArray(Players, flags);
        dest.writeString(Description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Server> CREATOR = new Creator<Server>() {
        @Override
        public Server createFromParcel(Parcel in) {
            return new Server(in);
        }

        @Override
        public Server[] newArray(int size) {
            return new Server[size];
        }
    };
}