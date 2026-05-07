# Display Hospital Logo in App UI

**Optional Enhancement:** Show your logo in the app screens

---

## Where to Display Logo

### Option 1: Home Screen Header
Add logo at the top of TemiMainScreen:

```kotlin
// In TemiMainScreen.kt
Column(
    modifier = Modifier
        .fillMaxWidth()
        .background(colorResource(R.color.dark_bg))
        .padding(16.dp)
) {
    // Hospital Logo
    Image(
        painter = painterResource(id = R.drawable.ic_launcher),
        contentDescription = "All Is Well Hospital Logo",
        modifier = Modifier
            .size(80.dp)
            .align(Alignment.CenterHorizontally)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Rest of home screen...
}
```

### Option 2: Splash Screen
Show logo when app starts:

```kotlin
// In a new SplashScreen.kt Composable
@Composable
fun SplashScreen(onNavigateToMain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.dark_bg)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "All Is Well Hospital",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
    
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToMain()
    }
}
```

### Option 3: Header Bar with Logo
Add to every screen:

```kotlin
// Reusable header composable
@Composable
fun AppHeaderWithLogo(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.dark_bg))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "Logo",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// Use in any screen:
Column {
    AppHeaderWithLogo("Navigation")
    // Rest of screen...
}
```

---

## Import Statement

Add to your Compose file:

```kotlin
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
```

---

## Size Recommendations

| Use Case | Size | Color Scheme |
|----------|------|-------------|
| App Icon | 48-192 dp (system) | Full color |
| Header | 48-64 dp | Full color |
| Splash Screen | 200-250 dp | Full color |
| Menu Card | 64-96 dp | Full color |
| Small Badge | 24-32 dp | Full color or monochrome |

---

## Logo with Transparent Background

For best results, use logo with **transparent background**:

1. Open your MiniLogoAllIsWell.jpg in:
   - Photoshop
   - GIMP (free)
   - Canva
   - Remove.bg (online)

2. **Remove white background** → Make transparent

3. **Export as PNG** with transparency

4. **Use in Compose:**
   ```kotlin
   Image(
       painter = painterResource(id = R.drawable.ic_launcher),
       contentDescription = "Logo",
       modifier = Modifier
           .size(80.dp)
           .background(Color.Transparent) // Transparent BG shows through
   )
   ```

---

## Complete Example: TemiMainScreen Header

```kotlin
// In TemiMainScreen.kt
@Composable
fun TemiMainScreen(
    robot: Robot? = null,
    onNavigate: (String) -> Unit = {},
    currentViseme: String = "viseme_sil",
    currentIntensity: Float = 0f
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.dark_bg))
    ) {
        // LOGO HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1a1a2e),
                            Color(0xFF16213e)
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "All Is Well Hospital",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = stringResource(R.string.hospital_title),
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "स्वागत है",
                    fontSize = 14.sp,
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        // Rest of screen (existing menu cards, etc.)
        // ...
    }
}
```

---

## Testing

After adding logo to UI:

1. **Rebuild:**
   ```bash
   ./gradlew installDebug
   ```

2. **Check:**
   - Logo displays at correct size
   - Logo has proper transparency (if applicable)
   - Logo doesn't obscure other content
   - Text is readable over/around logo

3. **Adjust size if needed:**
   - Change `.size(100.dp)` to larger/smaller
   - Change padding values
   - Change corner radius (RoundedCornerShape)

---

## File References

The logo is stored at:
```
app/src/main/res/drawable/ic_launcher.png
app/src/main/res/mipmap-*/ic_launcher.png
```

Reference in code as:
```kotlin
painterResource(id = R.drawable.ic_launcher)
```

---

## Result

✅ Professional app icon on home screen  
✅ Logo in app drawer  
✅ Logo visible in app header  
✅ Branded experience for patients  
✅ Hospital identity visible to all users  

---

**Status:** Optional Enhancement - For Professional Branding  
**Difficulty:** Easy  
**Impact:** High - Professional appearance!


