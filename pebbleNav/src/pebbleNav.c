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
  TextLayer mainTextLayer;

  AppSync sync;
  uint8_t sync_buffer[32];
} NavData;

enum {
  STREET_DATA_KEY = 0,
};
TextLayer mainTextLayer;


// TODO: Error handling
static void sync_error_callback(DictionaryResult dict_error, AppMessageResult app_message_error, void *context) {
}

static void sync_tuple_changed_callback(const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context) {

  switch (key) {
  case STREET_DATA_KEY:
    text_layer_set_text(&NavData.mainTextLayer, new_tuple->value->cstring);
    break;
  default:
    return;
  }
}

void handle_init(AppContextRef ctx) {

  text_layer_init(&NavData.mainTextLayer, GRect(0, 100, 144, 68));
  text_layer_set_font(&NavData.mainTextLayer, fonts_get_system_font(FONT_KEY_ROBOTO_CONDENSED_21));
  text_layer_set_text(&NavData.mainTextLayer, "waiting for input...");

  window_init(&NavData.window, "Window Name");
  layer_add_child(&NavData.window.layer, &mainTextLayer.layer);
    
  Tuplet initial_values[] = {                                                                                                               
    TupletCString(STREET_DATA_KEY, "waiting for data")                                                                                      
  };    
  app_sync_init(&NavData.sync, NavData.sync_buffer, sizeof(NavData.sync_buffer), initial_values, ARRAY_LENGTH(initial_values),                             sync_tuple_changed_callback, sync_error_callback, NULL);            

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
