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

  AppSync sync;
  uint8_t sync_buffer[64];
} NavData;

char debugLog[10];

enum {
  TURN_DATA_KEY = 0,
  STREET_DATA_KEY = 1, 
  TURN_IMAGE_KEY = 2,
  VIBE_KEY = 3
};

// TODO: Error handling
static void sync_error_callback(DictionaryResult dict_error, AppMessageResult app_message_error, void *context) {
}

static void sync_tuple_changed_callback(const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context) {

  switch (key) {
  case TURN_DATA_KEY:
    text_layer_set_text(&NavData.turnTextLayer, new_tuple->value->cstring);
    break;
  case STREET_DATA_KEY:
    text_layer_set_text(&NavData.streetTextLayer, new_tuple->value->cstring);
    break;
  case TURN_IMAGE_KEY:
    break;
  case VIBE_KEY:
    snprintf(debugLog, 10, "%lu", new_tuple->value->uint32);
    text_layer_set_text(&NavData.streetTextLayer, debugLog);
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

  text_layer_init(&NavData.turnTextLayer, GRect(0, 90, 144, 27));
  text_layer_set_font(&NavData.turnTextLayer, fonts_get_system_font(FONT_KEY_ROBOTO_CONDENSED_21));
  text_layer_set_text(&NavData.turnTextLayer, "nav input...");

  text_layer_init(&NavData.streetTextLayer, GRect(0, 117, 144, 27));
  text_layer_set_font(&NavData.streetTextLayer, fonts_get_system_font(FONT_KEY_ROBOTO_CONDENSED_21));
  text_layer_set_text(&NavData.streetTextLayer, "street input...");

  window_init(&NavData.window, "pebbleNav");
  layer_add_child(&NavData.window.layer, &NavData.streetTextLayer.layer);
  layer_add_child(&NavData.window.layer, &NavData.turnTextLayer.layer);

  Tuplet initial_values[] = {                                                                                                               
    TupletCString(STREET_DATA_KEY, "waiting for data"),
    TupletCString(TURN_DATA_KEY, "nav input...")
  };    
  app_sync_init(&NavData.sync, NavData.sync_buffer, sizeof(NavData.sync_buffer), initial_values, ARRAY_LENGTH(initial_values), 
		sync_tuple_changed_callback, sync_error_callback, NULL);            

  window_stack_push(&NavData.window, true /* Animated */);
  
}


void pbl_main(void *params) {
  PebbleAppHandlers handlers = {
    .init_handler = &handle_init,
    .messaging_info = {
      .buffer_sizes = {
        .inbound = 64,
        .outbound = 16,
      }
    }

  };
  app_event_loop(params, &handlers);
}
