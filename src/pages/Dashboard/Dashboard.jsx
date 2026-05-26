import { Box, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { getAllGames } from "./services/games.service";

export default function Dashboard() {
    const [games, setGames] = useState([]);

    useEffect(() => {
        const fetchGames = async () => {
            try {
                const gamesData = await getAllGames();
                setGames(gamesData);
            } catch (error) {
                console.error('Error fetching games:', error);
            }
        };

        fetchGames();
    }, []);

    return (
        <Box style={{ height: "100vh", display: "flex", alignItems: "center", flexDirection: "column" }}>
            <Typography variant="h1" style={{ fontSize: "2rem" }}>Estoy en el dashboard!</Typography>
            <Typography variant="h2" style={{ fontSize: "1.5rem" }}>Games: {games.length}</Typography>
            {
                games.map(game => (
                    <Box key={game.id} style={{ border: "1px solid black", padding: "1rem", margin: "0.5rem", width: "80%" }}>
                        <Typography variant="h3" style={{ fontSize: "1.2rem" }}>{game.name}</Typography>
                    </Box>
                ))
            }
        </Box>
    );
}