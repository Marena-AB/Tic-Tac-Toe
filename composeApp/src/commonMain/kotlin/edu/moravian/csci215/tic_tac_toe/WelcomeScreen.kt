package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tictactoe.composeapp.generated.resources.*

/** The four selectable player types shown in each dropdown. */
@Composable
private fun playerTypes() = listOf(
    stringResource(Res.string.human),
    stringResource(Res.string.easy_ai),
    stringResource(Res.string.medium_ai),
    stringResource(Res.string.hard_ai),
)

/** Returns a randomly chosen player name. */
private fun randomName(): String = RANDOM_NAMES.random()

/**
 * The welcome screen that lets users configure both players and launch a new game.
 * There is intentionally no top app bar on this screen.
 *
 * @param paddingValues insets supplied by the outer Scaffold
 * @param onStartGame   called with (p1Type, p1Name, p2Type, p2Name) when Start is tapped
 *                      and both names are non-blank
 * @param showSnackbar  suspending callback to display a snackbar message via the shared host
 */
@Composable
fun WelcomeScreen(
    paddingValues: PaddingValues,
    onStartGame: (String, String, String, String) -> Unit,
    showSnackbar: suspend (String) -> Unit,
) {
    val playerTypes = playerTypes()
    var player1Type by remember { mutableStateOf(playerTypes[0]) }
    var player1Name by remember { mutableStateOf(randomName()) }
    var player2Type by remember { mutableStateOf(playerTypes[0]) }
    var player2Name by remember { mutableStateOf(randomName()) }
    val scope = rememberCoroutineScope()
    val emptyNameError = stringResource(Res.string.empty_name_error)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(paddingValues),
    ) {
        val isLandscape = maxWidth > maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Hide the logo in landscape to save vertical space
            if (!isLandscape) {
                XOLogo()
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = stringResource(Res.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
            ) {
                PlayerSetupColumn(
                    label = stringResource(Res.string.player_1_label),
                    nameLabel = stringResource(Res.string.player_1_name),
                    selectedType = player1Type,
                    onTypeSelected = { player1Type = it },
                    name = player1Name,
                    onNameChanged = { player1Name = it },
                    playerTypes = playerTypes,
                )
                PlayerSetupColumn(
                    label = stringResource(Res.string.player_2_label),
                    nameLabel = stringResource(Res.string.player_2_name),
                    selectedType = player2Type,
                    onTypeSelected = { player2Type = it },
                    name = player2Name,
                    onNameChanged = { player2Name = it },
                    playerTypes = playerTypes,
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            StartButton(
                onClick = {
                    if (player1Name.isBlank() || player2Name.isBlank()) {
                        scope.launch { showSnackbar(emptyNameError) }
                    } else {
                        onStartGame(player1Type, player1Name, player2Type, player2Name)
                    }
                },
            )
        }
    }
}

/**
 * App logo displayed at the top of the welcome screen.
 */
@Composable
private fun XOLogo() {
    Image(
        painter = painterResource(Res.drawable.tic_tac_toe),
        contentDescription = stringResource(Res.string.app_title),
        modifier = Modifier.size(120.dp),
    )
}

/**
 * A labeled column containing a [PlayerTypeDropDown] and a [PlayerNameTextField] for one player.
 *
 * @param label          heading shown above the controls (e.g. "Player 1")
 * @param nameLabel      placeholder/label inside the name text field
 * @param selectedType   the currently selected player type
 * @param onTypeSelected callback when the user picks a new type
 * @param name           current name text
 * @param onNameChanged  callback when the name text changes
 * @param playerTypes    list of available player type strings
 */
@Composable
private fun PlayerSetupColumn(
    label: String,
    nameLabel: String,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    name: String,
    onNameChanged: (String) -> Unit,
    playerTypes: List<String>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        PlayerTypeDropDown(
            selectedType = selectedType,
            onTypeSelected = onTypeSelected,
            playerTypes = playerTypes,
        )
        PlayerNameTextField(
            label = nameLabel,
            name = name,
            onNameChanged = onNameChanged,
        )
    }
}

/**
 * Dropdown button that lets the user choose a player type.
 *
 * @param selectedType   the currently displayed player type
 * @param onTypeSelected callback invoked with the newly chosen type
 * @param playerTypes    list of available player type strings
 */
@Composable
private fun PlayerTypeDropDown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    playerTypes: List<String>,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text(selectedType)
            Spacer(modifier = Modifier.width(4.dp))
            // Material icon for the dropdown arrow — no hard-coded string needed
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            playerTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * Text field for entering or editing a player's name.
 *
 * @param label         label shown inside the field
 * @param name          current text value
 * @param onNameChanged callback when text changes
 */
@Composable
private fun PlayerNameTextField(
    label: String,
    name: String,
    onNameChanged: (String) -> Unit,
) {
    TextField(
        value = name,
        onValueChange = onNameChanged,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.width(140.dp),
    )
}

/**
 * The "Start!" button at the bottom of the welcome screen.
 *
 * @param onClick called when the button is tapped
 */
@Composable
private fun StartButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .defaultMinSize(minWidth = 140.dp)
            .height(52.dp),
    ) {
        Text(
            text = stringResource(Res.string.start_button),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}
