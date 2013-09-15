#include "pebble_os.h"
#include "pebble_app.h"
#include "pebble_fonts.h"


#define MY_UUID { 0xB1, 0x00, 0xC9, 0x29, 0xAF, 0xA1, 0x4B, 0xBA, 0xB4, 0x8A, 0x66, 0x8F, 0xAF, 0x70, 0x78, 0x8A }
PBL_APP_INFO(MY_UUID,
             "pebbleNav", "HackCMU",
             1, 0, /* App version */
             DEFAULT_MENU_ICON,
             APP_INFO_STANDARD_APP);

static struct NavData {
  Window window;
  TextLayer streetTextLayer;
  TextLayer turnTextLayer;

  BitmapLayer turn_layer;
  uint8_t current_turn;
  HeapBitmap turn_bitmap;
  
  AppSync sync;
  uint8_t sync_buffer[96];
} NavData;

static uint32_t TURN_ICONS[] = {
  RESOURCE_ID_RIGHT,
  RESOURCE_ID_LEFT,
  RESOURCE_ID_STRAIGHT
};

char debugLog[10];

enum {
  TURN_DATA_KEY = 0,
  STREET_DATA_KEY = 1, 
  TURN_IMAGE_KEY = 2,
  VIBE_KEY = 3
};

static void load_bitmap(uint8_t resource_id) {
  // If that resource is already the current icon, we don't need to reload it
  if (NavData.current_turn == resource_id) {
    return;
  }
  // Only deinit the current bitmap if a bitmap was previously loaded
  if (NavData.current_turn != 0) {
    heap_bitmap_deinit(&NavData.turn_bitmap);
  }
  // Keep track of what the current icon is
  NavData.current_turn = resource_id;
  // Load the new icon
  heap_bitmap_init(&NavData.turn_bitmap, resource_id);
}


// TODO: Error handling
static void sync_error_callback(DictionaryResult dict_error, AppMessageResult app_message_error, void *context) {
}

static void sync_tuple_changed_callback(const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context) {

  switch (key) {
  case TURN_DATA_KEY:
    text_layer_set_text(&NavData.turnTextLayer, new_tuple->value->cstring);
    break;
  case STREET_DATA_KEY:
    //    text_layer_set_text(&NavData.streetTextLayer, new_tuple->value->cstring);
    break;
  case TURN_IMAGE_KEY:
    load_bitmap(TURN_ICONS[new_tuple->value->uint8]);
    bitmap_layer_set_bitmap(&NavData.turn_layer, &NavData.turn_bitmap.bmp);
    break;
  case VIBE_KEY:
    snprintf(debugLog, 10, "%lu", new_tuple->value->uint32);
    //   text_layer_set_text(&NavData.streetTextLayer, debugLog);
    if (new_tuple->value->uint32 == 1)
      {
	vibes_short_pulse();
      }
    else if (new_tuple->value->uint32 == 2)
      {
	vibes_double_pulse();
      }
    else if (new_tuple->value->uint32 == 3)
      {
	vibes_long_pulse();
      }
    break;
  default:
    return;
  }
}

void handle_init(AppContextRef ctx) {

  GRect turn_rect = (GRect) {(GPoint) {32, 10}, (GSize) {80, 80}};
  bitmap_layer_init(&NavData.turn_layer, turn_rect);
  layer_add_child(&NavData.window.layer, &NavData.turn_layer.layer);

  text_layer_init(&NavData.turnTextLayer, GRect(0, 90, 144, 54));
  text_layer_set_font(&NavData.turnTextLayer, fonts_get_system_font(FONT_KEY_GOTHIC_14_BOLD));
  text_layer_set_text(&NavData.turnTextLayer, "nav input...");

  /*  text_layer_init(&NavData.streetTextLayer, GRect(0, 117, 144, 27));
  text_layer_set_font(&NavData.streetTextLayer, fonts_get_system_font(FONT_KEY_ROBOTO_CONDENSED_21));
  text_layer_set_text(&NavData.streetTextLayer, "street input..."); */
  text_layer_set_overflow_mode(&NavData.turnTextLayer, GTextOverflowModeWordWrap);

  window_init(&NavData.window, "pebbleNav");
  // layer_add_child(&NavData.window.layer, &NavData.streetTextLayer.layer);
  layer_add_child(&NavData.window.layer, &NavData.turnTextLayer.layer);

  Tuplet initial_values[] = {                                                                                                               
    TupletCString(STREET_DATA_KEY, "waiting for data"),
    TupletCString(TURN_DATA_KEY, "nav input...")
  };    
  app_sync_init(&NavData.sync, NavData.sync_buffer, sizeof(NavData.sync_buffer), initial_values, ARRAY_LENGTH(initial_values), 
		sync_tuple_changed_callback, sync_error_callback, NULL);            

  window_stack_push(&NavData.window, true /* Animated */);
  
}

static void handle_deinit(AppContextRef c) {
  app_sync_deinit(&NavData.sync);
  if (NavData.current_turn != 0) {
    heap_bitmap_deinit(&NavData.turn_bitmap);
  }
}


void pbl_main(void *params) {
  PebbleAppHandlers handlers = {
    .init_handler = &handle_init,
    .deinit_handler = &handle_deinit,
    .messaging_info = {
      .buffer_sizes = {
        .inbound = 64,
        .outbound = 16,
      }
    }

  };
  app_event_loop(params, &handlers);
}
